import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

//	Fahri Sahin		22120205001
//	Burak Guven		22120205016
//	Emre Said Yuce		22120205014

// Metotların tanımlandığı class:
class commands {

	// Dosya taşıma metodu:
	public static void fileMoving(String oldFolderPath, String newFolderPath, String folderExtension) {

		File oldFolder = new File(oldFolderPath);
		File[] files;

		if (folderExtension.equals("All")) {
			files = oldFolder.listFiles();
		} else {
			files = oldFolder.listFiles((dir, name) -> name.endsWith(folderExtension));
		}

		for (File file : files) {
			File newFile = new File(newFolderPath + File.separator + file.getName());
			try {
				// Dosyaları taşıyoruz
				Files.move(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// Dosya şifreleme metodu:
	public static void fileEncryption(String newFolderPath, String folderExtension) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {

		/*
		 * Not: Belirttiğiniz klasör yolu içerisindeki dosyalar içerisinde, yalnızca
		 * şifrelenebilecek formattaki dosyalar şifrelenir. Her dosya türü şifrelenmeye
		 * uygun değildir.
		 */

		File folder = new File(newFolderPath);
		File[] files;

		if (folderExtension.equals("All")) {
			files = folder.listFiles();
		} else {
			files = folder.listFiles((dir, name) -> name.endsWith(folderExtension));
		}

		String key = "0123456789abcdef";
		SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);

		for (File file : files) {
			if (folderExtension.equals("All")) {
				if (file.isFile()) {
					FileInputStream inputStream = new FileInputStream(file);
					byte[] buffer = new byte[1024];
					int bytesRead;

					String outputFilePath = newFolderPath + File.separator + file.getName() + ".encrypted";
					FileOutputStream outputStream = new FileOutputStream(outputFilePath);
					CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);

					while ((bytesRead = inputStream.read(buffer)) >= 0) {
						cipherOutputStream.write(buffer, 0, bytesRead);
					}

					inputStream.close();
					cipherOutputStream.flush();
					cipherOutputStream.close();

					// Dosyanın eski konumundan silinmesi
					file.delete();
				}
			} else {
				if (file.isFile() && file.getName().endsWith(folderExtension)) {
					FileInputStream inputStream = new FileInputStream(file);
					byte[] buffer = new byte[1024];
					int bytesRead;

					String outputFilePath = newFolderPath + File.separator + file.getName() + ".encrypted";
					FileOutputStream outputStream = new FileOutputStream(outputFilePath);
					CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);

					while ((bytesRead = inputStream.read(buffer)) >= 0) {
						cipherOutputStream.write(buffer, 0, bytesRead);
					}

					inputStream.close();
					cipherOutputStream.flush();
					cipherOutputStream.close();

					// Dosyanın eski konumundan silinmesi
					file.delete();
				}
			}
		}
	}

	// Dosya zipleme metodu:
	public static void fileConvertToZip(String oldFolderPath, String folderExtension) throws IOException {

		/*
		 * Not: Belirttiğiniz klasör yolu içerisindeki dosyalar içerisinde, herhangi bir
		 * klasör mevcutsa hata alabilirsiniz. Çözüm adına, klasör ayarlarından erişim
		 * ayarlarını düzenleyiniz. Eğer belirtilen klasör yolu içerisinde, erişim
		 * yetkisi sorunu yaşamayacağınız dosyalar mevcutsa metot sorunsuz çalışacaktır.
		 */

		String fileName;
		File directory = new File(oldFolderPath);
		if (!directory.isDirectory()) {
			throw new IllegalArgumentException("Path is not a directory");
		}

		File[] files;
		if (folderExtension.equals("All")) {
			files = directory.listFiles();
			fileName = "Tum Dosyalar";
		} else {
			files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(folderExtension));
			fileName = files[0].getName();
		}

		if (files == null || files.length == 0) {
			return;
		}

		String zipFileName = fileName + ".zip";
		File zipFile = new File(oldFolderPath + "/" + zipFileName); // zip dosyasının kaydedileceği konum
		FileOutputStream fos = new FileOutputStream(zipFile);
		ZipOutputStream zos = new ZipOutputStream(fos);
		byte[] buffer = new byte[1024];
		for (File file : files) {
			FileInputStream fis = new FileInputStream(file);
			zos.putNextEntry(new ZipEntry(file.getName()));
			int length;
			while ((length = fis.read(buffer)) > 0) {
				zos.write(buffer, 0, length);
			}
			zos.closeEntry();
			fis.close();
		}

		// Eski dosyayı silme metodu.
		for (File file : files) {
			if (folderExtension.equals("All")) {
				if (file.isFile()) {
					file.delete();
				}
			} else {
				if (file.isFile() && file.getName().toLowerCase().endsWith(folderExtension)) {
					file.delete();
				}
			}

		}

		zos.close();
		fos.close();
	}

	// Dosya gizleme metodu:
	public static void fileHiding(String oldFolderPath, String folderExtension) {

		/*
		 * Not: Bu metod, Windows'ta çalışıyor.
		 */

		try {
			Path folder = Paths.get(oldFolderPath);
			if (folderExtension.equals("All")) {
				folderExtension = "";
				DirectoryStream<Path> stream = Files.newDirectoryStream(folder, "*" + folderExtension);
				for (Path file : stream) {
					if (System.getProperty("os.name").contains("Windows")) {
						Files.setAttribute(file, "dos:hidden", true);
					}
				}
				stream.close();
			} else {
				DirectoryStream<Path> stream = Files.newDirectoryStream(folder, "*" + folderExtension);
				for (Path file : stream) {
					if (System.getProperty("os.name").contains("Windows")) {
						Files.setAttribute(file, "dos:hidden", true);
					}
				}
				stream.close();
			}

		} catch (Exception e) {
			System.out.println("Dosya gizleme hatası: " + e.getMessage());
		}
	}

}

public class Main extends JFrame {

	private JLabel oldAddressLabel, oldAddressTextLabel, newAddressLabel, newAddressTextLabel, checkBoxLabel, radioButtonLabel, creators;
	private JCheckBox moveAndEncryptCheckBox, moveAndZipCheckBox, moveAndHideCheckBox, moveCheckBox;
	private JRadioButton allFilesRadioButton, onlyTxtRadioButton, onlyPdfRadioButton, onlyDocxRadioButton;
	private JFileChooser fileChooser;
	private JButton oldAddressButton, newAddressButton, runButton;
	private JFrame frame;

	// Program içerisindeki bileşenlerin ve bileşenlerin fonksiyonlarının, boyutlarının, konumlarının tanımlanması:
	public Main() {
		
		frame = new JFrame("Dosya Tasiyici");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 650);
		frame.setLayout(null);
		frame.setResizable(false); // Program penceresinin boyutunun sabit kalmasını sağlar.
		frame.setVisible(true);

		fileChooser = new JFileChooser();

		oldAddressLabel = new JLabel("Eski Klasor Adresi:");
		oldAddressTextLabel = new JLabel();
		oldAddressTextLabel.setBackground(Color.WHITE);
		oldAddressTextLabel.setOpaque(true);
		oldAddressTextLabel.setBorder(BorderFactory.createEtchedBorder());
		oldAddressButton = new JButton("Dosya konumunu seçiniz.");
		oldAddressButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addressButtonClicked(oldAddressTextLabel);
			}
		});

		newAddressLabel = new JLabel("Yeni Klasor Adresi:");
		newAddressTextLabel = new JLabel();
		newAddressTextLabel.setBackground(Color.WHITE);
		newAddressTextLabel.setOpaque(true);
		newAddressTextLabel.setBorder(BorderFactory.createEtchedBorder());
		newAddressButton = new JButton("Dosya konumunu seçiniz.");
		newAddressButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addressButtonClicked(newAddressTextLabel);
			}
		});

		checkBoxLabel = new JLabel("Islem Secenekleri:");
		checkBoxLabel.setForeground(Color.BLACK);
		moveAndEncryptCheckBox = new JCheckBox("Tasi ve Sifrele");
		moveAndZipCheckBox = new JCheckBox("Tasi ve Sıkıştır (Ziple)");
		moveAndHideCheckBox = new JCheckBox("Tasi ve Gizle");
		moveCheckBox = new JCheckBox("Yalnizca Tasi");

		ButtonGroup checkBoxGroup = new ButtonGroup();
		checkBoxGroup.add(moveAndEncryptCheckBox);
		checkBoxGroup.add(moveAndZipCheckBox);
		checkBoxGroup.add(moveAndHideCheckBox);
		checkBoxGroup.add(moveCheckBox);

		radioButtonLabel = new JLabel("Dosya Turu Secenekleri:");
		radioButtonLabel.setForeground(Color.BLACK);
		allFilesRadioButton = new JRadioButton("Tüm dosyalar");
		onlyTxtRadioButton = new JRadioButton("Sadece '.txt' dosyalar");
		onlyPdfRadioButton = new JRadioButton("Sadece '.pdf' dosyalar");
		onlyDocxRadioButton = new JRadioButton("Sadece '.docx' dosyalar");

		ButtonGroup radioButtonGroup = new ButtonGroup();
		radioButtonGroup.add(allFilesRadioButton);
		radioButtonGroup.add(onlyTxtRadioButton);
		radioButtonGroup.add(onlyPdfRadioButton);
		radioButtonGroup.add(onlyDocxRadioButton);

		creators = new JLabel("bu program fahri, burak ve emre tarafindan gelistirilmistir.");
		creators.setFont(new Font(creators.getFont().getName(), Font.ITALIC, creators.getFont().getSize()));
		creators.setForeground(Color.RED);

		runButton = new JButton("Calistir");
		runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					runButtonClicked();
				} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		oldAddressLabel.setBounds(100, 60, 400, 30);
		oldAddressTextLabel.setBounds(100, 90, 370, 40);
		oldAddressButton.setBounds(100, 130, 180, 30);

		newAddressLabel.setBounds(100, 260, 400, 30);
		newAddressTextLabel.setBounds(100, 290, 370, 40);
		newAddressButton.setBounds(100, 330, 180, 30);

		checkBoxLabel.setBounds(100, 420, 120, 20);
		moveAndEncryptCheckBox.setBounds(100, 450, 150, 20);
		moveAndZipCheckBox.setBounds(100, 480, 150, 20);
		moveAndHideCheckBox.setBounds(100, 510, 150, 20);
		moveCheckBox.setBounds(100, 540, 150, 20);

		radioButtonLabel.setBounds(500, 60, 150, 20);
		allFilesRadioButton.setBounds(500, 90, 200, 20);
		onlyTxtRadioButton.setBounds(500, 120, 200, 20);
		onlyPdfRadioButton.setBounds(500, 150, 200, 20);
		onlyDocxRadioButton.setBounds(500, 180, 200, 20);

		creators.setBounds(450, 380, 400, 400);

		runButton.setBounds(500, 450, 200, 50);

		frame.add(oldAddressLabel);
		frame.add(oldAddressTextLabel);
		frame.add(oldAddressButton);
		frame.add(newAddressLabel);
		frame.add(newAddressTextLabel);
		frame.add(newAddressButton);
		frame.add(checkBoxLabel);
		frame.add(moveAndEncryptCheckBox);
		frame.add(moveAndZipCheckBox);
		frame.add(moveAndHideCheckBox);
		frame.add(moveCheckBox);
		frame.add(radioButtonLabel);
		frame.add(allFilesRadioButton);
		frame.add(onlyTxtRadioButton);
		frame.add(onlyPdfRadioButton);
		frame.add(onlyDocxRadioButton);
		frame.add(runButton);
		frame.add(creators);

	}

	// Dosya konumu seçim işlemlerinin yapılması:
	private void addressButtonClicked(JLabel AddressLabel) {
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = fileChooser.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFolder = fileChooser.getSelectedFile();
			AddressLabel.setText(selectedFolder.getAbsolutePath());
			// Seçilen klasörün yolunu gösterir.
		}

	}

	// Programın çalıştırılması ile meydana gelen işlemler:
	private void runButtonClicked() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IOException {

		String oldAddressText = oldAddressTextLabel.getText();
		String newAddressText = newAddressTextLabel.getText();

		// Taşımak istenen dosyanın, türü seçili değil ise gelecek olan uyarı kutusu.
		if (allFilesRadioButton.isSelected() || onlyTxtRadioButton.isSelected() || onlyPdfRadioButton.isSelected() || onlyDocxRadioButton.isSelected()) {
		} else {
			JOptionPane.showMessageDialog(this, "Tasimak istediginiz dosya turunu seciniz!", "Uyari", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		// Taşıma metodu seçili değil ise gelecek olan uyarı kutusu.
		if (moveAndEncryptCheckBox.isSelected() || moveAndZipCheckBox.isSelected() || moveAndHideCheckBox.isSelected() || moveCheckBox.isSelected()) {
		} else {
			JOptionPane.showMessageDialog(this, "Tasima metodu seciniz!", "Uyari", JOptionPane.WARNING_MESSAGE);
			return;
		}

		// Taşımak istenen dosyanın, konumlarından herhangi biri seçili değil ise gelecek olan uyarı kutusu.
		if (oldAddressText.isEmpty() || newAddressText.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Lutfen dosya konumlarini giriniz!", "Uyari", JOptionPane.WARNING_MESSAGE);
			return;
		}

		// Taşımak istenen dosyanın, konumları aynı ise gelecek olan uyarı kutusu.
		if (oldAddressText.equals(newAddressText)) {
			JOptionPane.showMessageDialog(this, "Dosya konumlari farkli olmalidir!", "Uyari", JOptionPane.WARNING_MESSAGE);
			return;
		}

		
		// Taşı ve Şifrele metodu seçilirse:
		if (moveAndEncryptCheckBox.isSelected()) {

			// Tüm dosyalara işlem yapılmak isteniyorsa:
			if (allFilesRadioButton.isSelected()) {
				commands.fileEncryption(oldAddressText, "All");
				commands.fileMoving(oldAddressText, newAddressText, "All");
				JOptionPane.showMessageDialog(null,
						"Tüm dosyalar taşındı ve şifrelenebilecek formattaki dosyalar şifrelendi.\nİşlem tamamlandı.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
			}

			// Yalnızca '.txt' uzantılı dosyalara işlem yapılmak isteniyorsa:
			else if (onlyTxtRadioButton.isSelected()) {
				commands.fileEncryption(oldAddressText, ".txt");
				commands.fileMoving(oldAddressText, newAddressText, ".txt.encrypted");
				JOptionPane.showMessageDialog(null, "Tüm '.txt' uzantılı dosyalar taşındı ve şifrelendi.\nİşlem tamamlandı.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
			}

			// Yalnızca '.pdf' uzantılı dosyalara işlem yapılmak isteniyorsa:
			else if (onlyPdfRadioButton.isSelected()) {
				commands.fileEncryption(oldAddressText, ".pdf");
				commands.fileMoving(oldAddressText, newAddressText, ".pdf.encrypted");
				JOptionPane.showMessageDialog(null, "Tüm '.pdf' uzantılı dosyalar taşındı ve şifrelendi.\nİşlem tamamlandı.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
			}

			// Yalnızca '.docx' uzantılı dosyalara işlem yapılmak isteniyorsa:
			else if (onlyDocxRadioButton.isSelected()) {
				commands.fileEncryption(oldAddressText, ".docx");
				commands.fileMoving(oldAddressText, newAddressText, ".docx.encrypted");
				JOptionPane.showMessageDialog(null, "Tüm '.docx' uzantılı dosyalar taşındı ve şifrelendi.\nİşlem tamamlandı.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
			}
		}

		
		// Taşı ve Ziple metodu seçilirse:
		if (moveAndZipCheckBox.isSelected()) {

			// Tüm dosyalara işlem yapılmak isteniyorsa:
			if (allFilesRadioButton.isSelected()) {
				try {
					commands.fileConvertToZip(oldAddressText, "All");
				} catch (IOException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, "HATA!!.", "HATA MESAJI", JOptionPane.WARNING_MESSAGE);
				}
				commands.fileMoving(oldAddressText, newAddressText, ".zip");
				JOptionPane.showMessageDialog(null, "Tüm dosyalar taşındı ve ziplendi.\nİşlem tamamlandı.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
			}

			// Yalnızca '.txt' uzantılı dosyalara işlem yapılmak isteniyorsa:
			else if (onlyTxtRadioButton.isSelected()) {
				try {
					commands.fileConvertToZip(oldAddressText, ".txt");
				} catch (IOException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, "HATA!!.", "HATA MESAJI", JOptionPane.WARNING_MESSAGE);
				}
				commands.fileMoving(oldAddressText, newAddressText, ".zip");
				JOptionPane.showMessageDialog(null, "Tüm '.txt' uzantılı dosyalar taşındı ve şifrelendi.\nİşlem tamamlandı.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
			}

			// Yalnızca '.pdf' uzantılı dosyalara işlem yapılmak isteniyorsa:
			else if (onlyPdfRadioButton.isSelected()) {
				try {
					commands.fileConvertToZip(oldAddressText, ".pdf");
				} catch (IOException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, "HATA!!.", "HATA MESAJI", JOptionPane.WARNING_MESSAGE);
				}
				commands.fileMoving(oldAddressText, newAddressText, ".zip");
				JOptionPane.showMessageDialog(null, "Tüm '.pdf' uzantılı dosyalar taşındı ve şifrelendi.\nİşlem tamamlandı.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
			}

			// Yalnızca '.docx' uzantılı dosyalara işlem yapılmak isteniyorsa:
			else if (onlyDocxRadioButton.isSelected()) {
				try {
					commands.fileConvertToZip(oldAddressText, ".docx");
				} catch (IOException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, "HATA!!.", "HATA MESAJI", JOptionPane.WARNING_MESSAGE);
				}
				commands.fileMoving(oldAddressText, newAddressText, ".zip");
				JOptionPane.showMessageDialog(null, "Tüm '.docx' uzantılı dosyalar taşındı ve şifrelendi.\nİşlem tamamlandı.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
			}
		}

		
		// Taşı ve Gizle metodu seçilirse:
		// NOT: Bu metod, yalnızca Windows'ta çalışıyor.
		if (moveAndHideCheckBox.isSelected()) {

			// Tüm dosyalara işlem yapılmak isteniyorsa:
			if (allFilesRadioButton.isSelected()) {
				commands.fileHiding(oldAddressText, "All");
				commands.fileMoving(oldAddressText, newAddressText, "All");
				JOptionPane.showMessageDialog(null, "Tüm dosyalar taşındı ve gizlendi.\nİşlem tamamlandı.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
			}

			// Yalnızca '.txt' uzantılı dosyalara işlem yapılmak isteniyorsa:
			else if (onlyTxtRadioButton.isSelected()) {
				commands.fileHiding(oldAddressText, ".txt");
				commands.fileMoving(oldAddressText, newAddressText, ".txt");
				JOptionPane.showMessageDialog(null, "Tüm '.txt' uzantılı dosyalar taşındı ve gizlendi.\nİşlem tamamlandı.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
			}

			// Yalnızca '.pdf' uzantılı dosyalara işlem yapılmak isteniyorsa:
			else if (onlyPdfRadioButton.isSelected()) {
				commands.fileHiding(oldAddressText, ".pdf");
				commands.fileMoving(oldAddressText, newAddressText, ".pdf");
				JOptionPane.showMessageDialog(null, "Tüm '.pdf' uzantılı dosyalar taşındı ve gizlendi.\nİşlem tamamlandı.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
			}

			// Yalnızca '.docx' uzantılı dosyalara işlem yapılmak isteniyorsa:
			else if (onlyDocxRadioButton.isSelected()) {
				commands.fileHiding(oldAddressText, ".docx");
				commands.fileMoving(oldAddressText, newAddressText, ".docx");
				JOptionPane.showMessageDialog(null, "Tüm '.docx' uzantılı dosyalar taşındı ve gizlendi.\nİşlem tamamlandı.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
			}
		}

		
		// Yalnızca Taşı metodu seçilirse:
		if (moveCheckBox.isSelected()) {

			// Tüm dosyalara işlem yapılmak isteniyorsa:
			if (allFilesRadioButton.isSelected()) {
				commands.fileMoving(oldAddressText, newAddressText, "All");
				JOptionPane.showMessageDialog(null, "Tüm dosyalar taşındı.\nİşlem tamamlandı.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
			}

			// Yalnızca '.txt' uzantılı dosyalara işlem yapılmak isteniyorsa:
			else if (onlyTxtRadioButton.isSelected()) {
				commands.fileMoving(oldAddressText, newAddressText, ".txt");
				JOptionPane.showMessageDialog(null, "Tüm '.txt' uzantılı dosyalar taşındı.\nİşlem tamamlandı.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
			}

			// Yalnızca '.pdf' uzantılı dosyalara işlem yapılmak isteniyorsa:
			else if (onlyPdfRadioButton.isSelected()) {
				commands.fileMoving(oldAddressText, newAddressText, ".pdf");
				JOptionPane.showMessageDialog(null, "Tüm '.pdf' uzantılı dosyalar taşındı.\nİşlem tamamlandı.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
			}

			// Yalnızca '.docx' uzantılı dosyalara işlem yapılmak isteniyorsa:
			else if (onlyDocxRadioButton.isSelected()) {
				commands.fileMoving(oldAddressText, newAddressText, ".docx");
				JOptionPane.showMessageDialog(null, "Tüm '.docx' uzantılı dosyalar taşındı.\nİşlem tamamlandı.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
			}
		}

		clear();
	}

	// Program çalıştırıldıktan sonra seçimlerin sıfırlanması:
	private void clear() {

		oldAddressTextLabel.setText("");
		newAddressTextLabel.setText("");

		ButtonGroup checkBoxGroup = new ButtonGroup();
		checkBoxGroup.add(moveAndEncryptCheckBox);
		checkBoxGroup.add(moveAndZipCheckBox);
		checkBoxGroup.add(moveAndHideCheckBox);
		checkBoxGroup.add(moveCheckBox);
		checkBoxGroup.clearSelection();

		ButtonGroup radioButtonGroup = new ButtonGroup();
		radioButtonGroup.add(allFilesRadioButton);
		radioButtonGroup.add(onlyTxtRadioButton);
		radioButtonGroup.add(onlyPdfRadioButton);
		radioButtonGroup.add(onlyDocxRadioButton);
		radioButtonGroup.clearSelection();

	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Main();
			}
		});
	}
}
