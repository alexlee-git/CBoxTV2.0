package tv.newtv.cboxtv.cms.details.model;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Hashtable;

/**
 * util for url convert to qrcode, use google zxing jar
 * 
 *
 * @author chulei
 */

public class QrcodeUtil {

	private int QR_WIDTH = 480; // pixel
	private int QR_HEIGHT = 480; // pixel

	public void createQRImage(String url, ImageView imgQrd, int nWidth, int nHeight) {
		QR_WIDTH = nWidth;
		QR_HEIGHT = nHeight;

		createQRImage(url, imgQrd);
	}

	public void createQRImage(String url, ImageView imgQrd) {
		try {
			if (url == null || "".equals(url) || url.length() < 1) {
				return;
			}
			
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
			
			for (int y = 0; y < QR_HEIGHT; y++) {
				for (int x = 0; x < QR_WIDTH; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * QR_WIDTH + x] = 0xff000000;
					} else {
						pixels[y * QR_WIDTH + x] = 0xffffffff;
					}
				}
			}
			
			Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
			imgQrd.setImageBitmap(bitmap);
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}

}
