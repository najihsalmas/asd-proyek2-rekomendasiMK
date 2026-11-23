/**
 * Satu mata kuliah. Pada graf prasyarat, objek ini adalah SIMPUL (vertex).
 */
public class Matakuliah {
    public final String kode;      
    public final String nama;      
    public final int sks;
    public final int semester;   
    public final String tawaran;  

    // Diisi saat membaca file mahasiswa (mhs_xx.txt).
    public boolean lulus = false;

    public Matakuliah(String kode, String nama, int sks, int semester, String tawaran) {
        this.kode = kode;
        this.nama = nama;
        this.sks = sks;
        this.semester = semester;
        this.tawaran = tawaran;
    }

    /** True jika MK ini dibuka pada semester berikutnya ("Ganjil" atau "Genap"). */
    public boolean ditawarkanPada(String semesterBerikutnya) {
        return tawaran.equalsIgnoreCase("Keduanya")
            || tawaran.equalsIgnoreCase(semesterBerikutnya);
    }

    /** Format baris daftar: "SO Sistem Operasi 5 SKS". */
    @Override
    public String toString() {
        return kode + " " + nama + " " + sks + " SKS";
    }

    /** Mengubah isi SinglyLinkedList (berisi Matakuliah) menjadi array Matakuliah[]. */
    public static Matakuliah[] dariDaftar(SinglyLinkedList daftar) {
        Object[] mentah = daftar.toArray();
        Matakuliah[] hasil = new Matakuliah[mentah.length];
        for (int i = 0; i < mentah.length; i++) {
            hasil[i] = (Matakuliah) mentah[i];
        }
        return hasil;
    }
}
