import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Membaca tiga jenis file masukan:
 *   matakuliah.csv : Kode;Nama;sks;semester;ganjil/genap   (pemisah titik koma)
 *   prasyarat.csv  : prasyarat;matakuliah                   (kolom 1 = prasyarat dari kolom 2)
 *   mhs_xx.txt     : semester_berikutnya=genap
 *                    lulus=RPL,PDAS,...
 * Baris kosong, spasi berlebih, CRLF/LF, dan baris judul (header) ditangani.
 */
public class PembacaFile {

    /** Membangun graf dari matakuliah.csv dan prasyarat.csv. */
    public static GrafPrasyarat bacaGraf(String fileMatakuliah, String filePrasyarat)
            throws IOException {
        GrafPrasyarat graf = new GrafPrasyarat();

        String[] baris = bacaBaris(fileMatakuliah);
        for (int i = 0; i < baris.length; i++) {
            String[] f = pisah(baris[i]);
            if (i == 0 && f.length >= 3 && !adalahAngka(f[2])) {
                continue;                                  
            }
            if (f.length < 5) {
                throw new IllegalArgumentException(fileMatakuliah + ": baris '" + baris[i]
                    + "' harus punya 5 kolom (Kode;Nama;sks;semester;ganjil/genap)");
            }
            if (!adalahAngka(f[2]) || !adalahAngka(f[3])) {
                throw new IllegalArgumentException(fileMatakuliah + ": sks/semester bukan angka pada baris '"
                    + baris[i] + "'");
            }
            graf.tambahMatakuliah(new Matakuliah(f[0], f[1],
                Integer.parseInt(f[2]), Integer.parseInt(f[3]), normalisasiTawaran(f[4], baris[i])));
        }

        baris = bacaBaris(filePrasyarat);
        for (int i = 0; i < baris.length; i++) {
            String[] f = pisah(baris[i]);
            if (i == 0 && f.length >= 1 && f[0].equalsIgnoreCase("prasyarat")) {
                continue;                                
            }
            if (f.length < 2) {
                throw new IllegalArgumentException(filePrasyarat + ": baris '" + baris[i]
                    + "' harus punya 2 kolom (prasyarat;matakuliah)");
            }
            if (!graf.tambahPrasyarat(f[0], f[1])) {
                System.err.println("Peringatan: prasyarat '" + f[0] + " -> " + f[1]
                    + "' diabaikan (kode tidak dikenal, sisi ganda, atau menunjuk diri sendiri)");
            }
        }
        return graf;
    }

    /**
     * Membaca file mahasiswa: menandai MK yang lulus pada graf dan mengembalikan
     * semester berikutnya ("Ganjil" atau "Genap").
     */
    public static String bacaMahasiswa(String fileMhs, GrafPrasyarat graf) throws IOException {
        String semester = null;
        String[] baris = bacaBaris(fileMhs);
        for (int i = 0; i < baris.length; i++) {
            int sama = baris[i].indexOf('=');
            if (sama < 0) {
                throw new IllegalArgumentException(fileMhs + ": baris '" + baris[i]
                    + "' tidak berformat kunci=nilai");
            }
            String kunci = baris[i].substring(0, sama).trim();
            String nilai = baris[i].substring(sama + 1).trim();

            if (kunci.equalsIgnoreCase("semester_berikutnya")) {
                if (nilai.equalsIgnoreCase("ganjil")) {
                    semester = "Ganjil";
                } else if (nilai.equalsIgnoreCase("genap")) {
                    semester = "Genap";
                } else {
                    throw new IllegalArgumentException(fileMhs
                        + ": semester_berikutnya harus 'ganjil' atau 'genap', bukan '" + nilai + "'");
                }
            } else if (kunci.equalsIgnoreCase("lulus")) {
                String[] kode = nilai.split(",");
                for (int k = 0; k < kode.length; k++) {
                    String kd = kode[k].trim();
                    if (kd.length() == 0) {
                        continue;
                    }
                    Matakuliah mk = graf.ambil(kd);
                    if (mk == null) {
                        System.err.println("Peringatan: kode MK '" + kd + "' pada " + fileMhs
                            + " tidak dikenal, diabaikan");
                    } else {
                        mk.lulus = true;
                    }
                }
            }
        }
        if (semester == null) {
            throw new IllegalArgumentException(fileMhs + ": baris semester_berikutnya tidak ditemukan");
        }
        return semester;
    }


    /** Membaca file (UTF-8) menjadi array baris tanpa baris kosong dan tanpa BOM. */
    private static String[] bacaBaris(String path) throws IOException {
        SinglyLinkedList baris = new SinglyLinkedList();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
            String s;
            boolean pertama = true;
            while ((s = br.readLine()) != null) {
                if (pertama && s.startsWith("\uFEFF")) {
                    s = s.substring(1);
                }
                pertama = false;
                s = s.trim();
                if (s.length() > 0) {
                    baris.addLast(s);
                }
            }
        }
        Object[] mentah = baris.toArray();
        String[] hasil = new String[mentah.length];
        for (int i = 0; i < mentah.length; i++) {
            hasil[i] = (String) mentah[i];
        }
        return hasil;
    }

    private static String[] pisah(String baris) {
        String[] f = baris.split(";", -1);
        for (int i = 0; i < f.length; i++) {
            f[i] = f[i].trim();
        }
        return f;
    }

    private static boolean adalahAngka(String s) {
        if (s.length() == 0) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) < '0' || s.charAt(i) > '9') {
                return false;
            }
        }
        return true;
    }

    private static String normalisasiTawaran(String s, String barisAsal) {
        if (s.equalsIgnoreCase("ganjil")) {
            return "Ganjil";
        }
        if (s.equalsIgnoreCase("genap")) {
            return "Genap";
        }
        if (s.equalsIgnoreCase("keduanya")) {
            return "Keduanya";
        }
        throw new IllegalArgumentException("Kolom ganjil/genap harus Ganjil, Genap, atau Keduanya: '"
            + barisAsal + "'");
    }
}
