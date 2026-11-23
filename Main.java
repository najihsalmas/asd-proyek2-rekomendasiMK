import java.io.IOException;

/**
 *   java Main data/matakuliah.csv data/prasyarat.csv data/mhs_01.txt
 */
public class Main {
    public static void main(String[] args) {
        if (args == null || args.length != 3) {
            System.err.println("Cara pakai: java Main <matakuliah.csv> <prasyarat.csv> <mhs_xx.txt>");
            System.exit(1);
        }
        try {
            GrafPrasyarat graf = PembacaFile.bacaGraf(args[0], args[1]);
            String semester = PembacaFile.bacaMahasiswa(args[2], graf);
            Rekomendasi rekomendasi = new Rekomendasi(graf, semester);
            rekomendasi.hitung();
            System.out.print(Penampil.format(rekomendasi));
        } catch (IOException e) {
            System.err.println("Gagal membaca file: " + e.getMessage());
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println("Data tidak valid: " + e.getMessage());
            System.exit(1);
        }
    }
}
