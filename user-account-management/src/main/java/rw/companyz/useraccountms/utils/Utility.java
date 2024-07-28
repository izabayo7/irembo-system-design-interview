package rw.companyz.useraccountms.utils;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Utility {
    private static MessageSource messageSource;
    public  String localize(String path) {
        return  Utility.messageSource.getMessage(path, null, LocaleContextHolder.getLocale());
    }
    public static ArrayList<String> readFileContents(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            ArrayList<String> lines = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            return lines;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Split an array into chunks
     * @param array
     * @param chunkSize
     * @return
     * @param <T>
     */

    public static <T> ArrayList<ArrayList<T>> chunkArray(ArrayList<T> array, int chunkSize) {
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("Chunk size must be greater than 0");
        }

        ArrayList<ArrayList<T>> chunks = new ArrayList<>();
        int numChunks = (int) Math.ceil((double) array.size() / chunkSize);

        for (int i = 0; i < numChunks; i++) {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, array.size());

            ArrayList<T> chunk = new ArrayList<>(array.subList(start, end));
            chunks.add(chunk);
        }

        return chunks;
    }

    /**
     * Sanitize phone number
     * @param unSanitizedPhoneNumber
     * @return
     */
    public static String sanitizePhoneNumber(String unSanitizedPhoneNumber){
        String sanitizedPhoneNumber = null;
        if(unSanitizedPhoneNumber != null && unSanitizedPhoneNumber.length() == 10 && unSanitizedPhoneNumber.startsWith("0")){
            sanitizedPhoneNumber = "+25" + unSanitizedPhoneNumber;
        }
        return sanitizedPhoneNumber;
    }

    /**
     * Sanitize service number
     * @param unSanitizedServiceNumber
     * @return
     */
    public static String sanitizeServiceNumber(String unSanitizedServiceNumber){
        String sanitizedServiceNumber = unSanitizedServiceNumber;

//        if(unSanitizedServiceNumber != null && !unSanitizedServiceNumber.isEmpty()){
//            if(unSanitizedServiceNumber.startsWith("#")){
//                sanitizedServiceNumber = unSanitizedServiceNumber.substring(1);
//            }else{
//                sanitizedServiceNumber = unSanitizedServiceNumber;
//            }
//        }

        if(sanitizedServiceNumber != null && sanitizedServiceNumber.startsWith("0")){
            sanitizedServiceNumber = sanitizedServiceNumber.replaceFirst("^0+(?!$)", "");
        }

        if(sanitizedServiceNumber == null || sanitizedServiceNumber.isEmpty()){
            sanitizedServiceNumber = generateRandomServiceNumber();
        }

        return sanitizedServiceNumber;
    }

    /**
     * Generate random service number
     * @return
     */
    public static String generateRandomServiceNumber(){
        return "auto_" + (int) (Math.random() * 1000000000);
    }

}
