package libraries;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import userdefinedexceptions.UserDefinedException;

public class Excelutility {

	public static HashMap<String, String> getDataFromExcel(final String Tc_id, final String filePath,
			final String sheetName) throws Exception {
		try (XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File(filePath)))) {
			return getDataFromExcel(workbook.getSheet(sheetName), Tc_id, filePath);
		}
	}

	public static HashMap<String, String> getDataFromExcel(final String Tc_id, final String filePath, final int sheetId)
			throws Exception {
		try (XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File(filePath)))) {
			return getDataFromExcel(workbook.getSheetAt(sheetId), Tc_id, filePath);
		}
	}

	private static HashMap<String, String> getDataFromExcel(XSSFSheet sheet, String Tc_id, String filePath)
			throws UserDefinedException {
		ArrayList<String> listOfTestCasesId = new ArrayList<>();
		ArrayList<String> listOfTestCaseHeader = new ArrayList<>();
		ArrayList<String> listOfTestCaseData = new ArrayList<>();
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			XSSFCell cell = sheet.getRow(i).getCell(0);
			listOfTestCasesId.add(getCellValue(cell));
		}
		XSSFRow firstRow = sheet.getRow(0);
		for (int i = 0; i < firstRow.getLastCellNum(); i++) {
			XSSFCell cell = firstRow.getCell(i);
			listOfTestCaseHeader.add(getCellValue(cell));
		}
		int index = listOfTestCasesId.indexOf(Tc_id);
		if (index == -1) {
			throw new UserDefinedException("There is no " + Tc_id
					+ " in the first column of the excel. File path of the excel is " + filePath);
		}
		index = index + 1;
		XSSFRow dataRow = sheet.getRow(index);
		for (int i = 0; i < listOfTestCaseHeader.size(); i++) {
			XSSFCell cell = dataRow.getCell(i);
			listOfTestCaseData.add(getCellValue(cell));
		}
		HashMap<String, String> testData = new HashMap<>();
		for (int i = 0; i < listOfTestCaseHeader.size(); i++) {
			testData.put(listOfTestCaseHeader.get(i), listOfTestCaseData.get(i));
		}
		return testData;
	}

	private static String getCellValue(XSSFCell cell) {
		if (null == cell)
			return null;
		switch (cell.getCellType()) {
		case BLANK:
			return null;
		case BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		case NUMERIC:
			return String.valueOf(cell.getNumericCellValue());
		case FORMULA:
			switch (cell.getCachedFormulaResultType()) {
			case BLANK:
				return null;
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

	public static void main(String[] args) throws Exception {
		getDataFromExcel("TC_001", "C:\\Work\\eclipse-workspace\\CommonLibrary\\external-files\\InputData.xlsx", 0);
	}
}