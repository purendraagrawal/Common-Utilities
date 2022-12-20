package libraries;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CompareExcelFiles {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		XSSFWorkbook workbook = null;
		FileOutputStream outputStream = null;
		try {
			String filePath = "C:\\Work\\eclipse-workspace\\Common-Utilities\\external-files\\workbook.xlsx";
			workbook = new XSSFWorkbook(new FileInputStream(new File(filePath)));
			XSSFSheet firstSheet = workbook.getSheetAt(0);
			XSSFSheet secondSheet = workbook.getSheetAt(1);
			if (compareRowsAndColumnsSize(firstSheet, secondSheet))
				compareData(workbook, firstSheet, secondSheet);
			else {
				System.out.println("Size not matched");
			}
			outputStream = new FileOutputStream(new File(filePath));
			workbook.write(outputStream);
		} finally {
			if (outputStream != null)
				outputStream.close();
			if (workbook != null)
				workbook.close();
		}
	}

	private static int getTotalRowsInSheet(XSSFSheet sheet) {
		return sheet.getLastRowNum();
	}

	private static int getTotalColumnsInSheet(XSSFSheet sheet) {
		return sheet.getRow(0).getLastCellNum();
	}

	private static int getTotalColumnsInSheet(XSSFSheet sheet, int rowNumber) {
		return sheet.getRow(rowNumber).getLastCellNum();
	}

	private static boolean compareRowsAndColumnsSize(XSSFSheet sheet1, XSSFSheet sheet2) {
		if (getTotalRowsInSheet(sheet1) == getTotalRowsInSheet(sheet2))
			if (getTotalColumnsInSheet(sheet1) == getTotalColumnsInSheet(sheet2))
				return true;
		return false;
	}

	private static CellStyle setBackgroundColorOfCellToRed(XSSFWorkbook workbook) {
		CellStyle style = workbook.createCellStyle();
		style.setFillForegroundColor(IndexedColors.RED.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		return style;
	}

	private static void compareData(XSSFWorkbook workbook, XSSFSheet sheet1, XSSFSheet sheet2)
			throws FileNotFoundException, IOException {
		for (int i = 0; i < getTotalRowsInSheet(sheet1); i++) {
			for (int j = 0; j < getTotalColumnsInSheet(sheet1, i); j++) {
				XSSFCell cellOfSheet1 = sheet1.getRow(i).getCell(j);
				XSSFCell cellOfSheet2 = sheet2.getRow(i).getCell(j);
				if (!getCellValue(cellOfSheet1).equals(getCellValue(cellOfSheet2))) {
					cellOfSheet2.setCellStyle(setBackgroundColorOfCellToRed(workbook));
				}
			}
		}
	}

	private static String getCellValue(XSSFCell cell) {
		if (null == cell)
			return null;
		switch (cell.getCellType()) {
		case BLANK:
			return "";
		case BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		case NUMERIC:
			return String.valueOf(cell.getNumericCellValue());
		case FORMULA:
			switch (cell.getCachedFormulaResultType()) {
			case BLANK:
				return "";
			case BOOLEAN:
				return String.valueOf(cell.getBooleanCellValue());
			case NUMERIC:
				return String.valueOf(cell.getNumericCellValue());
			default:
				return cell.getStringCellValue();
			}
		default:
			return cell.getStringCellValue();
		}
	}
}
