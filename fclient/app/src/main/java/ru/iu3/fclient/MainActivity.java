package ru.iu3.fclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import ru.iu3.fclient.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'fclient' library on application startup.
    static {
        System.loadLibrary("fclient");
        System.loadLibrary("mbedcrypto");
    }

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        int res =initRng();
        byte[] v = randomBytes(10);
        System.out.println(res + " " + v.toString() + System.lineSeparator());

        byte[] key = randomBytes(20);
        byte[] data = {10, 20, 30 ,40};
        System.out.println("Data - " + data.toString() + "  Key - " + key.toString() + System.lineSeparator());
        byte[] encrypted_data = encrypt(key, data);
        System.out.println("Encrypted data - " + encrypted_data.toString() + System.lineSeparator());
        byte[] decrypted_data = decrypt(key, encrypted_data);
        System.out.println("Decrypted data - " + decrypted_data + System.lineSeparator());
        // Example of a call to a native method
        TextView tv = binding.sampleText;
        tv.setText(stringFromJNI());
    }

    /**
     * A native method that is implemented by the 'fclient' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public static native int initRng();
    public static native byte[] randomBytes(int no);

    public static native byte[] encrypt(byte[] key, byte[] data);
    public static native byte[] decrypt(byte[] key, byte[] data);

}