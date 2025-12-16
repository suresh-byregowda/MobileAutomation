package utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public final class CsvUtils {

    private CsvUtils() {}

    /* =======================================================
       READ CSV AS MAP (HEADER → VALUE)
       ======================================================= */
    public static List<Map<String, String>> readCsv(String csvKey) {

        String resourcePath = ConfigReader.get(csvKey);
        if (resourcePath == null) {
            throw new RuntimeException("CSV path not found in config for key: " + csvKey);
        }

        try (InputStream is = CsvUtils.class
                .getClassLoader()
                .getResourceAsStream(resourcePath)) {

            if (is == null) {
                throw new RuntimeException("CSV file not found on classpath: " + resourcePath);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            List<Map<String, String>> data = new ArrayList<>();

            String line;
            String[] headers = null;
            int rowIndex = 0;

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",", -1);

                if (rowIndex == 0) {
                    headers = values;
                } else {
                    Map<String, String> row = new LinkedHashMap<>();
                    for (int i = 0; i < headers.length; i++) {
                        row.put(
                                headers[i].trim(),
                                i < values.length ? values[i].trim() : ""
                        );
                    }
                    data.add(row);
                }
                rowIndex++;
            }
            return data;

        } catch (Exception e) {
            throw new RuntimeException("Failed to read CSV for key: " + csvKey, e);
        }
    }

    /* =======================================================
       READ CSV RAW (ARRAY FORMAT)
       ======================================================= */
    public static List<String[]> readCsvRaw(String csvKey) {

        String resourcePath = ConfigReader.get(csvKey);
        if (resourcePath == null) {
            throw new RuntimeException("CSV path not found in config for key: " + csvKey);
        }

        try (InputStream is = CsvUtils.class
                .getClassLoader()
                .getResourceAsStream(resourcePath)) {

            if (is == null) {
                throw new RuntimeException("CSV file not found on classpath: " + resourcePath);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            List<String[]> data = new ArrayList<>();

            String line;
            while ((line = br.readLine()) != null) {
                data.add(line.split(",", -1));
            }
            return data;

        } catch (Exception e) {
            throw new RuntimeException("Failed to read CSV for key: " + csvKey, e);
        }
    }
}
