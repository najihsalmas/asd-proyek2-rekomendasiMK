/**
 * Logika inti: menentukan MK mana yang bisa diambil dan mana yang belum
 * (beserta alasannya) pada semester berikutnya.
 */
public class Rekomendasi {
    public static final String KODE_SKRIPSI = "SKR";

    private final GrafPrasyarat graf;
    private final String semesterBerikutnya;
    private SinglyLinkedList bisaDiambil = new SinglyLinkedList();
    private SinglyLinkedList belumBisa = new SinglyLinkedList();

    public Rekomendasi(GrafPrasyarat graf, String semesterBerikutnya) {
        if (graf == null) {
            throw new IllegalArgumentException("Graf prasyarat tidak boleh null");
        }
        if (semesterBerikutnya == null || semesterBerikutnya.trim().isEmpty()) {
            throw new IllegalArgumentException("Semester berikutnya tidak boleh kosong");
        }
        this.graf = graf;
        this.semesterBerikutnya = semesterBerikutnya.trim();
    }

    /** Menghitung hasil. Aman dipanggil berulang kali. */
    public void hitung() {
        bisaDiambil = new SinglyLinkedList();
        belumBisa = new SinglyLinkedList();

        for (int i = 0; i < graf.jumlah(); i++) {
            Matakuliah mk = graf.get(i);
            if (mk == null || mk.lulus || !mk.ditawarkanPada(semesterBerikutnya)) {
                continue;
            }
            Matakuliah[] kurang;
            if (mk.kode != null && mk.kode.equalsIgnoreCase(KODE_SKRIPSI)) {
                kurang = mkWajibLainBelumLulus(mk);
            } else {
                kurang = graf.prasyaratBelumLulus(mk);
            }
            if (kurang == null || kurang.length == 0) {
                bisaDiambil.addLast(mk);
            } else {
                belumBisa.addLast(new Penolakan(mk, kurang));
            }
        }
    }

    public String semesterBerikutnya() {
        return semesterBerikutnya;
    }

    public Matakuliah[] bisaDiambil() {
        return Matakuliah.dariDaftar(bisaDiambil);
    }

    public Penolakan[] belumBisa() {
        Object[] mentah = belumBisa.toArray();
        Penolakan[] hasil = new Penolakan[mentah.length];
        for (int i = 0; i < mentah.length; i++) {
            hasil[i] = (Penolakan) mentah[i];
        }
        return hasil;
    }

    /** Semua MK selain skripsi yang belum lulus (syarat khusus skripsi). */
    private Matakuliah[] mkWajibLainBelumLulus(Matakuliah skripsi) {
        SinglyLinkedList sisa = new SinglyLinkedList();
        for (int i = 0; i < graf.jumlah(); i++) {
            Matakuliah mk = graf.get(i);
            if (mk == null) {
                continue;
            }
            boolean samaDenganSkripsi = (mk == skripsi)
                || (mk.kode != null && skripsi != null && skripsi.kode != null
                    && mk.kode.equalsIgnoreCase(skripsi.kode));
            if (!samaDenganSkripsi && !mk.lulus) {
                sisa.addLast(mk);
            }
        }
        return Matakuliah.dariDaftar(sisa);
    }
}
