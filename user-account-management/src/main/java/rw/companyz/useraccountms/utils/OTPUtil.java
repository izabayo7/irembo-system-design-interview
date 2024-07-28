package rw.companyz.useraccountms.utils;

import java.util.Random;

/**
 * Utility class for OTP
 */
public class OTPUtil {



    public static String  generateOtp(){
        Random random = new Random();
        int otpValue = 100_000 + random.nextInt(900_000);
        String otp = String.valueOf(otpValue);

        return otp;
    }
}
