package com.ruoyi.seating.support;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.seating.domain.SeatAssignment;
import com.ruoyi.seating.domain.SeatClassroom;
import com.ruoyi.seating.domain.SeatPlan;
import com.ruoyi.seating.domain.SeatPosition;
import com.ruoyi.seating.domain.SeatStudent;

/**
 * 座位方案 PDF 导出器。
 *
 * <p>采用先绘制座位表图片，再将图片嵌入单页 PDF 的方式，避免前端浏览器打印的安全限制。</p>
 */
public class SeatPlanPdfExporter
{
    private static final int PAGE_WIDTH = 842;

    private static final int PAGE_HEIGHT = 595;

    private static final int PAGE_MARGIN = 24;

    private static final int TITLE_HEIGHT = 84;

    private static final int LABEL_COLUMN_WIDTH = 44;

    private static final int LABEL_ROW_HEIGHT = 32;

    private static final int LABEL_GAP = 8;

    private static final int CELL_WIDTH = 148;

    private static final int CELL_HEIGHT = 92;

    private static final int CELL_GAP = 10;

    private static final int PLATFORM_SIZE = 34;

    private static final int PLATFORM_GAP = 12;

    private static final int PLATFORM_BAR_LENGTH = 240;

    private static final Color PAGE_BORDER = new Color(220, 223, 230);

    private static final Color TITLE_COLOR = new Color(48, 49, 51);

    private static final Color META_COLOR = new Color(96, 98, 102);

    private static final Color LABEL_BG = new Color(250, 250, 250);

    private static final Color LABEL_TEXT = new Color(144, 147, 153);

    private static final Font TITLE_FONT = new Font("Microsoft YaHei", Font.BOLD, 22);

    private static final Font META_FONT = new Font("Microsoft YaHei", Font.PLAIN, 13);

    private static final Font LABEL_FONT = new Font("Microsoft YaHei", Font.BOLD, 12);

    private static final Font CODE_FONT = new Font("Microsoft YaHei", Font.PLAIN, 12);

    private static final Font NAME_FONT = new Font("Microsoft YaHei", Font.BOLD, 17);

    private static final Font LOCK_FONT = new Font("Microsoft YaHei", Font.PLAIN, 12);

    private static final Font PLATFORM_FONT = new Font("Microsoft YaHei", Font.BOLD, 15);

    public byte[] exportSeatPlanPdf(SeatPlan plan, SeatClassroom classroom, List<SeatPosition> positions,
            Map<Long, SeatAssignment> assignmentMap, Map<Long, SeatStudent> studentMap, String viewMode)
    {
        if (positions == null || positions.isEmpty())
        {
            throw new ServiceException("教室没有座位布局");
        }
        BufferedImage image = renderSeatPlanImage(plan, classroom, positions, assignmentMap, studentMap, viewMode);
        byte[] jpegBytes = encodeJpeg(image);
        return buildPdf(jpegBytes, image.getWidth(), image.getHeight());
    }

    private BufferedImage renderSeatPlanImage(SeatPlan plan, SeatClassroom classroom, List<SeatPosition> positions,
            Map<Long, SeatAssignment> assignmentMap, Map<Long, SeatStudent> studentMap, String viewMode)
    {
        Map<String, SeatPosition> positionMap = buildPositionMap(positions);
        int maxRow = positions.stream().mapToInt(item -> item.getRowIndex().intValue()).max().orElse(1);
        int maxCol = positions.stream().mapToInt(item -> item.getColIndex().intValue()).max().orElse(1);
        String normalizedViewMode = normalizeViewMode(viewMode);
        String platformPosition = classroom == null || StringUtils.isBlank(classroom.getPlatformPosition())
                ? "FRONT" : classroom.getPlatformPosition();
        if (isStudentView(normalizedViewMode))
        {
            platformPosition = reversePlatformPosition(platformPosition);
        }

        boolean horizontalPlatform = "FRONT".equals(platformPosition) || "BACK".equals(platformPosition);
        boolean verticalPlatform = "LEFT".equals(platformPosition) || "RIGHT".equals(platformPosition);
        int gridWidth = maxCol * CELL_WIDTH + Math.max(maxCol - 1, 0) * CELL_GAP;
        int gridHeight = maxRow * CELL_HEIGHT + Math.max(maxRow - 1, 0) * CELL_GAP;
        int shellWidth = LABEL_COLUMN_WIDTH + LABEL_GAP + gridWidth;
        int shellHeight = LABEL_ROW_HEIGHT + LABEL_GAP + gridHeight;
        int width = PAGE_MARGIN * 2 + shellWidth + (verticalPlatform ? PLATFORM_SIZE + PLATFORM_GAP : 0);
        int height = PAGE_MARGIN * 2 + TITLE_HEIGHT + shellHeight + (horizontalPlatform ? PLATFORM_SIZE + PLATFORM_GAP : 0);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        try
        {
            applyRenderingHints(g);
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);
            g.setColor(PAGE_BORDER);
            g.drawRect(0, 0, width - 1, height - 1);

            drawTitle(g, plan, normalizedViewMode, width);
            drawMeta(g, plan, width);

            int shellX = PAGE_MARGIN + ("LEFT".equals(platformPosition) ? PLATFORM_SIZE + PLATFORM_GAP : 0);
            int shellY = PAGE_MARGIN + TITLE_HEIGHT + ("FRONT".equals(platformPosition) ? PLATFORM_SIZE + PLATFORM_GAP : 0);
            int seatGridX = shellX + LABEL_COLUMN_WIDTH + LABEL_GAP;
            int seatGridY = shellY + LABEL_ROW_HEIGHT + LABEL_GAP;

            drawPlatform(g, platformPosition, shellX, shellY, shellWidth, shellHeight);
            drawSeatFrame(g, positionMap, assignmentMap, studentMap, maxRow, maxCol, normalizedViewMode,
                    shellX, shellY, seatGridX, seatGridY);
        }
        finally
        {
            g.dispose();
        }
        return image;
    }

    private void drawTitle(Graphics2D g, SeatPlan plan, String viewMode, int width)
    {
        g.setFont(TITLE_FONT);
        g.setColor(TITLE_COLOR);
        drawTextWithEllipsis(g, plan.getPlanName() == null ? "座位方案" : plan.getPlanName(),
                PAGE_MARGIN, PAGE_MARGIN + 24, width - PAGE_MARGIN * 2);
        g.setFont(META_FONT);
        g.setColor(META_COLOR);
        String meta = "班级：" + defaultText(plan.getClassName())
                + "    教室：" + defaultText(plan.getClassroomName())
                + "    视角：" + resolveViewModeLabel(viewMode)
                + "    总评分：" + (plan.getTotalScore() == null ? "-" : plan.getTotalScore());
        drawTextWithEllipsis(g, meta, PAGE_MARGIN, PAGE_MARGIN + 52, width - PAGE_MARGIN * 2);
    }

    private void drawMeta(Graphics2D g, SeatPlan plan, int width)
    {
        g.setColor(PAGE_BORDER);
        g.drawLine(PAGE_MARGIN, PAGE_MARGIN + TITLE_HEIGHT - 10, width - PAGE_MARGIN, PAGE_MARGIN + TITLE_HEIGHT - 10);
    }

    private void drawPlatform(Graphics2D g, String platformPosition, int shellX, int shellY, int shellWidth,
            int shellHeight)
    {
        if ("FRONT".equals(platformPosition))
        {
            drawPlatformRect(g, shellX + shellWidth / 2 - PLATFORM_BAR_LENGTH / 2,
                    shellY - PLATFORM_SIZE - 12, PLATFORM_BAR_LENGTH, PLATFORM_SIZE, "讲台");
        }
        else if ("BACK".equals(platformPosition))
        {
            drawPlatformRect(g, shellX + shellWidth / 2 - PLATFORM_BAR_LENGTH / 2,
                    shellY + shellHeight + 12, PLATFORM_BAR_LENGTH, PLATFORM_SIZE, "讲台");
        }
        else if ("LEFT".equals(platformPosition))
        {
            drawPlatformRect(g, shellX - PLATFORM_SIZE - 12, shellY, PLATFORM_SIZE, shellHeight, "讲台");
        }
        else if ("RIGHT".equals(platformPosition))
        {
            drawPlatformRect(g, shellX + shellWidth + 12, shellY, PLATFORM_SIZE, shellHeight, "讲台");
        }
    }

    private void drawPlatformRect(Graphics2D g, int x, int y, int width, int height, String text)
    {
        g.setColor(new Color(64, 158, 255));
        g.fill(new RoundRectangle2D.Double(x, y, width, height, 8, 8));
        g.setColor(new Color(64, 158, 255));
        g.setStroke(new BasicStroke(1.2f));
        g.draw(new RoundRectangle2D.Double(x, y, width, height, 8, 8));
        g.setFont(PLATFORM_FONT);
        g.setColor(Color.WHITE);
        drawCenteredString(g, text, x, y, width, height);
    }

    private void drawSeatFrame(Graphics2D g, Map<String, SeatPosition> positionMap,
            Map<Long, SeatAssignment> assignmentMap, Map<Long, SeatStudent> studentMap, int maxRow, int maxCol,
            String viewMode, int shellX, int shellY, int seatGridX, int seatGridY)
    {
        drawLabelBadge(g, shellX, shellY, LABEL_COLUMN_WIDTH, LABEL_ROW_HEIGHT, "");
        drawColumnLabels(g, buildDisplayIndices(maxCol, viewMode), seatGridX, shellY);
        drawRowLabels(g, buildDisplayIndices(maxRow, viewMode), shellX, seatGridY);

        for (int rowIndex = 1; rowIndex <= maxRow; rowIndex++)
        {
            int sourceRowIndex = isStudentView(viewMode) ? maxRow - rowIndex + 1 : rowIndex;
            for (int colIndex = 1; colIndex <= maxCol; colIndex++)
            {
                int sourceColIndex = isStudentView(viewMode) ? maxCol - colIndex + 1 : colIndex;
                SeatPosition position = positionMap.get(sourceRowIndex + "-" + sourceColIndex);
                SeatAssignment assignment = position == null ? null : assignmentMap.get(position.getSeatId());
                int x = seatGridX + (colIndex - 1) * (CELL_WIDTH + CELL_GAP);
                int y = seatGridY + (rowIndex - 1) * (CELL_HEIGHT + CELL_GAP);
                drawSeatCell(g, position, assignment, studentMap, x, y, CELL_WIDTH, CELL_HEIGHT);
            }
        }
    }

    private void drawColumnLabels(Graphics2D g, List<Integer> labels, int x, int y)
    {
        for (int i = 0; i < labels.size(); i++)
        {
            drawLabelBadge(g, x + i * (CELL_WIDTH + CELL_GAP), y, CELL_WIDTH, LABEL_ROW_HEIGHT,
                    String.valueOf(labels.get(i)));
        }
    }

    private void drawRowLabels(Graphics2D g, List<Integer> labels, int x, int y)
    {
        for (int i = 0; i < labels.size(); i++)
        {
            drawLabelBadge(g, x, y + i * (CELL_HEIGHT + CELL_GAP), LABEL_COLUMN_WIDTH, CELL_HEIGHT,
                    String.valueOf(labels.get(i)));
        }
    }

    private void drawLabelBadge(Graphics2D g, int x, int y, int width, int height, String text)
    {
        g.setColor(LABEL_BG);
        g.fill(new RoundRectangle2D.Double(x, y, width, height, 6, 6));
        g.setColor(PAGE_BORDER);
        g.setStroke(new BasicStroke(1f));
        g.draw(new RoundRectangle2D.Double(x, y, width, height, 6, 6));
        if (StringUtils.isNotBlank(text))
        {
            g.setFont(LABEL_FONT);
            g.setColor(LABEL_TEXT);
            drawCenteredString(g, text, x, y, width, height);
        }
    }

    private void drawSeatCell(Graphics2D g, SeatPosition position, SeatAssignment assignment, Map<Long, SeatStudent> studentMap,
            int x, int y, int width, int height)
    {
        SeatCellStyle style = resolveSeatCellStyle(position, assignment);
        g.setColor(style.background);
        g.fill(new RoundRectangle2D.Double(x, y, width, height, 8, 8));
        g.setColor(style.border);
        g.setStroke(new BasicStroke(1.2f));
        g.draw(new RoundRectangle2D.Double(x, y, width, height, 8, 8));

        if (position != null)
        {
            g.setFont(CODE_FONT);
            g.setColor(style.secondaryText);
            drawTextWithEllipsis(g, defaultSeatCode(position), x + 10, y + 18, width - 20);
        }

        g.setFont(NAME_FONT);
        g.setColor(style.mainText);
        if (position == null)
        {
            return;
        }
        if ("2".equals(position.getSeatType()))
        {
            drawCenteredString(g, "过道", x, y, width, height);
            return;
        }
        if (!"0".equals(position.getSeatType()) || !"1".equals(position.getIsAvailable()) || "1".equals(position.getStatus()))
        {
            drawCenteredString(g, "不可用", x, y, width, height);
            return;
        }
        if (assignment == null)
        {
            drawCenteredString(g, "空座", x, y, width, height);
            return;
        }

        String studentText = buildStudentText(assignment, studentMap);
        drawCenteredString(g, studentText, x + 10, y + 26, width - 20, height - 46);
        if ("1".equals(assignment.getIsLocked()))
        {
            g.setFont(LOCK_FONT);
            g.setColor(new Color(184, 130, 48));
            drawTextWithEllipsis(g, "已锁定", x + 10, y + height - 12, width - 20);
        }
    }

    private SeatCellStyle resolveSeatCellStyle(SeatPosition position, SeatAssignment assignment)
    {
        if (position == null)
        {
            return new SeatCellStyle(new Color(255, 255, 255), PAGE_BORDER, META_COLOR, META_COLOR);
        }
        if ("2".equals(position.getSeatType()))
        {
            return new SeatCellStyle(new Color(244, 244, 245), new Color(192, 196, 204), LABEL_TEXT, LABEL_TEXT);
        }
        if (!"0".equals(position.getSeatType()) || !"1".equals(position.getIsAvailable()) || "1".equals(position.getStatus()))
        {
            return new SeatCellStyle(new Color(254, 240, 240), new Color(245, 108, 108), new Color(245, 108, 108),
                    new Color(245, 108, 108));
        }
        if (assignment == null)
        {
            return new SeatCellStyle(Color.WHITE, PAGE_BORDER, META_COLOR, LABEL_TEXT);
        }
        if ("1".equals(assignment.getIsLocked()))
        {
            return new SeatCellStyle(new Color(253, 246, 236), new Color(230, 162, 60), TITLE_COLOR, LABEL_TEXT);
        }
        return new SeatCellStyle(new Color(236, 245, 255), new Color(64, 158, 255), TITLE_COLOR, LABEL_TEXT);
    }

    private String buildStudentText(SeatAssignment assignment, Map<Long, SeatStudent> studentMap)
    {
        String studentName = defaultText(assignment.getStudentNameSnapshot());
        SeatStudent student = studentMap == null ? null : studentMap.get(assignment.getStudentId());
        String gender = resolveGenderLabel(student == null ? null : student.getGender());
        if (StringUtils.isBlank(gender))
        {
            return studentName;
        }
        return gender + " " + studentName;
    }

    private String resolveGenderLabel(String gender)
    {
        if ("0".equals(gender))
        {
            return "男";
        }
        if ("1".equals(gender))
        {
            return "女";
        }
        return "";
    }

    private void applyRenderingHints(Graphics2D g)
    {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    private void drawCenteredString(Graphics2D g, String text, int x, int y, int width, int height)
    {
        String content = StringUtils.defaultString(text);
        FontMetrics metrics = g.getFontMetrics();
        String fitted = fitText(content, metrics, Math.max(width - 8, 0));
        int textWidth = metrics.stringWidth(fitted);
        int textHeight = metrics.getHeight();
        int drawX = x + Math.max((width - textWidth) / 2, 0);
        int drawY = y + Math.max((height - textHeight) / 2, 0) + metrics.getAscent();
        g.drawString(fitted, drawX, drawY);
    }

    private void drawTextWithEllipsis(Graphics2D g, String text, int x, int y, int maxWidth)
    {
        FontMetrics metrics = g.getFontMetrics();
        String fitted = fitText(StringUtils.defaultString(text), metrics, maxWidth);
        g.drawString(fitted, x, y);
    }

    private String fitText(String text, FontMetrics metrics, int maxWidth)
    {
        if (metrics.stringWidth(text) <= maxWidth)
        {
            return text;
        }
        String clipped = text;
        while (!clipped.isEmpty() && metrics.stringWidth(clipped + "...") > maxWidth)
        {
            clipped = clipped.substring(0, clipped.length() - 1);
        }
        return clipped.isEmpty() ? "..." : clipped + "...";
    }

    private Map<String, SeatPosition> buildPositionMap(List<SeatPosition> positions)
    {
        Map<String, SeatPosition> positionMap = new HashMap<>();
        for (SeatPosition position : positions)
        {
            positionMap.put(position.getRowIndex() + "-" + position.getColIndex(), position);
        }
        return positionMap;
    }

    private List<Integer> buildDisplayIndices(int maxValue, String viewMode)
    {
        List<Integer> labels = new ArrayList<>(maxValue);
        if (isStudentView(viewMode))
        {
            for (int value = maxValue; value >= 1; value--)
            {
                labels.add(value);
            }
            return labels;
        }
        for (int value = 1; value <= maxValue; value++)
        {
            labels.add(value);
        }
        return labels;
    }

    private String normalizeViewMode(String viewMode)
    {
        return "STUDENT".equalsIgnoreCase(viewMode) ? "STUDENT" : "TEACHER";
    }

    private boolean isStudentView(String viewMode)
    {
        return "STUDENT".equals(viewMode);
    }

    private String reversePlatformPosition(String platformPosition)
    {
        if ("FRONT".equals(platformPosition))
        {
            return "BACK";
        }
        if ("BACK".equals(platformPosition))
        {
            return "FRONT";
        }
        if ("LEFT".equals(platformPosition))
        {
            return "RIGHT";
        }
        if ("RIGHT".equals(platformPosition))
        {
            return "LEFT";
        }
        return "FRONT";
    }

    private String resolveViewModeLabel(String viewMode)
    {
        return isStudentView(viewMode) ? "学生视角" : "教师视角";
    }

    private String defaultSeatCode(SeatPosition position)
    {
        if (StringUtils.isNotBlank(position.getSeatCode()))
        {
            return position.getSeatCode();
        }
        return position.getRowIndex() + "-" + position.getColIndex();
    }

    private String defaultText(Object value)
    {
        return value == null ? "-" : value.toString();
    }

    private byte[] encodeJpeg(BufferedImage image)
    {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream())
        {
            if (!ImageIO.write(image, "jpg", outputStream))
            {
                throw new ServiceException("无法编码座位表图片");
            }
            return outputStream.toByteArray();
        }
        catch (IOException e)
        {
            throw new ServiceException("编码座位表图片失败：" + e.getMessage());
        }
    }

    private byte[] buildPdf(byte[] jpegBytes, int imageWidth, int imageHeight)
    {
        double availableWidth = PAGE_WIDTH - PAGE_MARGIN * 2.0;
        double availableHeight = PAGE_HEIGHT - PAGE_MARGIN * 2.0;
        double scale = Math.min(availableWidth / imageWidth, availableHeight / imageHeight);
        double drawWidth = imageWidth * scale;
        double drawHeight = imageHeight * scale;
        double drawX = PAGE_MARGIN + (availableWidth - drawWidth) / 2.0;
        double drawY = PAGE_MARGIN + (availableHeight - drawHeight) / 2.0;

        ByteArrayOutputStream pdf = new ByteArrayOutputStream();
        List<Integer> offsets = new ArrayList<>();
        try
        {
            writeAscii(pdf, "%PDF-1.4\n");
            writeAscii(pdf, "%âãÏÓ\n");

            offsets.add(pdf.size());
            writeAscii(pdf, "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");

            offsets.add(pdf.size());
            writeAscii(pdf, "2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n");

            offsets.add(pdf.size());
            writeAscii(pdf, "3 0 obj\n");
            writeAscii(pdf, "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 " + PAGE_WIDTH + " " + PAGE_HEIGHT
                    + "] /Resources 4 0 R /Contents 5 0 R >>\nendobj\n");

            offsets.add(pdf.size());
            writeAscii(pdf, "4 0 obj\n<< /ProcSet [/PDF /ImageC] /XObject << /Im0 6 0 R >> >>\nendobj\n");

            String content = "q\n"
                    + formatPdfNumber(drawWidth) + " 0 0 " + formatPdfNumber(drawHeight) + " "
                    + formatPdfNumber(drawX) + " " + formatPdfNumber(drawY) + " cm\n"
                    + "/Im0 Do\nQ\n";
            byte[] contentBytes = content.getBytes(StandardCharsets.US_ASCII);
            offsets.add(pdf.size());
            writeAscii(pdf, "5 0 obj\n<< /Length " + contentBytes.length + " >>\nstream\n");
            pdf.write(contentBytes, 0, contentBytes.length);
            writeAscii(pdf, "endstream\nendobj\n");

            offsets.add(pdf.size());
            writeAscii(pdf, "6 0 obj\n<< /Type /XObject /Subtype /Image /Width " + imageWidth + " /Height " + imageHeight
                    + " /ColorSpace /DeviceRGB /BitsPerComponent 8 /Filter /DCTDecode /Length " + jpegBytes.length
                    + " >>\nstream\n");
            pdf.write(jpegBytes, 0, jpegBytes.length);
            writeAscii(pdf, "\nendstream\nendobj\n");

            int xrefStart = pdf.size();
            writeAscii(pdf, "xref\n0 7\n");
            writeAscii(pdf, "0000000000 65535 f \n");
            for (Integer offset : offsets)
            {
                writeAscii(pdf, String.format("%010d 00000 n \n", offset));
            }
            writeAscii(pdf, "trailer\n<< /Size 7 /Root 1 0 R >>\nstartxref\n");
            writeAscii(pdf, String.valueOf(xrefStart));
            writeAscii(pdf, "\n%%EOF");
            return pdf.toByteArray();
        }
        catch (IOException e)
        {
            throw new ServiceException("生成 PDF 失败：" + e.getMessage());
        }
    }

    private String formatPdfNumber(double value)
    {
        return String.format(java.util.Locale.US, "%.2f", value);
    }

    private void writeAscii(ByteArrayOutputStream outputStream, String value) throws IOException
    {
        outputStream.write(value.getBytes(StandardCharsets.US_ASCII));
    }

    private static class SeatCellStyle
    {
        private final Color background;

        private final Color border;

        private final Color mainText;

        private final Color secondaryText;

        private SeatCellStyle(Color background, Color border, Color mainText, Color secondaryText)
        {
            this.background = background;
            this.border = border;
            this.mainText = mainText;
            this.secondaryText = secondaryText;
        }
    }
}
