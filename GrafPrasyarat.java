public class GrafPrasyarat {
    private Matakuliah[] simpul;
    private SinglyLinkedList[] prasyarat;
    private int jumlah = 0;

    public GrafPrasyarat() {
        this(16);
    }

    public GrafPrasyarat(int kapasitasAwal) {
        if (kapasitasAwal < 1) {
            kapasitasAwal = 1;
        }
        simpul = new Matakuliah[kapasitasAwal];
        prasyarat = new SinglyLinkedList[kapasitasAwal];
    }

    public int jumlah() {
        return jumlah;
    }

    // Mengambil MK ke-i menurut urutan dimasukkan (urutan di matakuliah.csv)
    public Matakuliah get(int i) {
        if (i < 0 || i >= jumlah) {
            throw new IndexOutOfBoundsException("Indeks: " + i + ", jumlah MK: " + jumlah);
        }
        return simpul[i];
    }

    // Menambah simpul baru. Kode MK ganda ditolak.
    public void tambahMatakuliah(Matakuliah mk) {
        if (cari(mk.kode) != -1) {
            throw new IllegalArgumentException("Kode MK ganda: " + mk.kode);
        }
        if (jumlah == simpul.length) {
            perbesar();
        }
        simpul[jumlah] = mk;
        prasyarat[jumlah] = new SinglyLinkedList();
        jumlah++;
    }

    // Menambah sisi: kodePrasyarat -> kodeMk.
    // return false jika salah satu kode tidak dikenal, sisi menunjuk dirinya sendiri,
    // atau sisi yang sama sudah ada.
    public boolean tambahPrasyarat(String kodePrasyarat, String kodeMk) {
        int a = cari(kodePrasyarat);
        int b = cari(kodeMk);
        if (a == -1 || b == -1 || a == b) {
            return false;
        }
        Object[] sudahAda = prasyarat[b].toArray();
        for (int i = 0; i < sudahAda.length; i++) {
            if (sudahAda[i] == simpul[a]) {
                return false;
            }
        }
        prasyarat[b].addLast(simpul[a]);
        return true;
    }

    // Mencari indeks simpul berdasarkan kode; -1 jika tidak ada.
    public int cari(String kode) {
        for (int i = 0; i < jumlah; i++) {
            if (simpul[i].kode.equalsIgnoreCase(kode)) {
                return i;
            }
        }
        return -1;
    }

    // Mengambil MK berdasarkan kode; null jika tidak ada.
    public Matakuliah ambil(String kode) {
        int i = cari(kode);
        return (i == -1) ? null : simpul[i];
    }

    // Prasyarat langsung dari sebuah MK. 
    public Matakuliah[] prasyaratLangsung(Matakuliah mk) {
        int i = cari(mk.kode);
        if (i == -1) {
            throw new IllegalArgumentException("MK tidak ada di graf: " + mk.kode);
        }
        return Matakuliah.dariDaftar(prasyarat[i]);
    }


    public Matakuliah[] prasyaratBelumLulus(Matakuliah mk) {
        int mulai = cari(mk.kode);
        if (mulai == -1) {
            throw new IllegalArgumentException("MK tidak ada di graf: " + mk.kode);
        }
        boolean[] dikunjungi = new boolean[jumlah];
        SinglyLinkedList hasil = new SinglyLinkedList();
        telusuri(mulai, mulai, dikunjungi, hasil);
        return Matakuliah.dariDaftar(hasil);
    }

    private void telusuri(int i, int mulai, boolean[] dikunjungi, SinglyLinkedList hasil) {
        dikunjungi[i] = true;
        Object[] daftar = prasyarat[i].toArray();
        for (int k = 0; k < daftar.length; k++) {
            Matakuliah p = (Matakuliah) daftar[k];
            int j = cari(p.kode);
            if (!p.lulus && !dikunjungi[j]) {
                telusuri(j, mulai, dikunjungi, hasil);
            }
        }
        if (i != mulai) {          // MK yang sedang diperiksa sendiri tidak ikut dicatat
            hasil.addLast(simpul[i]);
        }
    }

    private void perbesar() {
        int baru = simpul.length * 2;
        Matakuliah[] s = new Matakuliah[baru];
        SinglyLinkedList[] p = new SinglyLinkedList[baru];
        System.arraycopy(simpul, 0, s, 0, jumlah);
        System.arraycopy(prasyarat, 0, p, 0, jumlah);
        simpul = s;
        prasyarat = p;
    }
}