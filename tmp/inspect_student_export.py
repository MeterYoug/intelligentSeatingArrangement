import json, urllib.request, zipfile, xml.etree.ElementTree as ET, os, tempfile
login_url = "http://127.0.0.1:8222/dev-api/login"
body = json.dumps({"username": "admin", "password": "admin123", "code": "", "uuid": ""}).encode("utf-8")
req = urllib.request.Request(login_url, data=body, headers={"Content-Type": "application/json;charset=utf-8"}, method="POST")
with urllib.request.urlopen(req) as resp:
    login = json.load(resp)
    token = login.get("token") or login.get("data", {}).get("token") or ""
print("login_keys", sorted(login.keys()))
print("has_token", bool(token))
export_url = "http://127.0.0.1:8222/dev-api/seating/plan/27/export-seat-table?viewMode=STUDENT"
req2 = urllib.request.Request(export_url, method="POST", headers={"Authorization": f"Bearer {token}"})
with urllib.request.urlopen(req2) as resp2:
    headers = dict(resp2.headers.items())
    content = resp2.read()
print("content_disposition", headers.get("Content-Disposition"))
out_path = os.path.join(tempfile.gettempdir(), "plan27-student.xlsx")
with open(out_path, "wb") as f:
    f.write(content)
print("saved", out_path, len(content))
ns = {"a": "http://schemas.openxmlformats.org/spreadsheetml/2006/main"}
with zipfile.ZipFile(out_path) as z:
    shared = []
    if "xl/sharedStrings.xml" in z.namelist():
        root = ET.fromstring(z.read("xl/sharedStrings.xml"))
        for si in root.findall("a:si", ns):
            text = "".join(t.text or "" for t in si.findall(".//a:t", ns))
            shared.append(text)
    sheet = ET.fromstring(z.read("xl/worksheets/sheet1.xml"))
    rows = []
    for row in sheet.findall(".//a:sheetData/a:row", ns):
        vals = []
        for c in row.findall("a:c", ns):
            ref = c.attrib.get("r")
            t = c.attrib.get("t")
            v = c.find("a:v", ns)
            is_el = c.find("a:is", ns)
            val = ""
            if t == "s" and v is not None:
                idx = int(v.text)
                val = shared[idx] if idx < len(shared) else f"<bad-shared:{idx}>"
            elif t == "inlineStr" and is_el is not None:
                val = "".join(x.text or "" for x in is_el.findall(".//a:t", ns))
            elif v is not None:
                val = v.text or ""
            vals.append((ref, val))
        rows.append((row.attrib.get("r"), vals))
    for r, vals in rows[:10]:
        print(r, vals[:14])
