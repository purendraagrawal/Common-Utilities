package libraries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CompareTwoFiles {
	/**
	 * Validate two file and Ignore the casing.
	 * 
	 * @param filePath1
	 *            - First file path
	 * @param filePath2
	 *            - Second file path
	 * @return true if both files are same.
	 * @throws IOException,
	 *             {@link NullPointerException}
	 *
	 */
	public static boolean validateTwoFiles(final String filePath1, final String filePath2) throws IOException {
		try (BufferedReader reader1 = new BufferedReader(new FileReader(new File(filePath1)));
				BufferedReader reader2 = new BufferedReader(new FileReader(new File(filePath2)));) {
			boolean areEqual = true;
			String line1 = reader1.readLine();
			String line2 = reader2.readLine();
			while (line1 != null || line2 != null) {
				if (line1 == null || line2 == null) {
					areEqual = false;
					break;
				}
				if (!line1.equalsIgnoreCase(line2)) {
					areEqual = false;
					break;
				}
				line1 = reader1.readLine();
				line2 = reader2.readLine();
			}
			return areEqual;
		}
	}
}
