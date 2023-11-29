import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Utils {
    
    public synchronized Map<String, String> readPasswordFile(String fileName) {
        Map<String, String> credMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] credData = line.split("\\s+");
                credMap.put(credData[0], credData[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
        return credMap;
    }

    public synchronized Map<String, Map<String, Integer>> readBalanceFile(String fileName) {
        Map<String, Map<String, Integer>> balanceMap = new LinkedHashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                Map<String, Integer> accountMap = new LinkedHashMap<>();
                String[] accountData = line.split("\\s+");
                accountMap.put("savings", Integer.parseInt(accountData[1]));
                accountMap.put("checking", Integer.parseInt(accountData[2]));
                balanceMap.put(accountData[0], accountMap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
        return balanceMap;
    }

    public synchronized void writeIntoFile(String fileName, String content) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            bw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }
}
