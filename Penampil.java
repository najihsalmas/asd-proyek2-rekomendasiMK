public class Penampil {
    public static String format(Rekomendasi r) {
        String hasil = "";

        hasil += "Daftar MK yang bisa diambil:\n";
        Matakuliah[] bisa = r.bisaDiambil();
        if (bisa.length == 0) {
            hasil += "(tidak ada)\n";
        }
        for (int i = 0; i < bisa.length; i++) {
            hasil += (i + 1) + ". " + bisa[i] + "\n";
        }

        hasil += "Daftar MK yang belum bisa diambil semester depan:\n";
        Penolakan[] belum = r.belumBisa();
        if (belum.length == 0) {
            hasil += "(tidak ada)\n";
        }
        for (int i = 0; i < belum.length; i++) {
            hasil += belum[i].mk.kode + ": ";
            if (belum[i].mk.kode.equalsIgnoreCase(Rekomendasi.KODE_SKRIPSI)) {
                hasil += "harus lulus semua MK wajib lainnya, belum lulus ";
            } else {
                hasil += "belum lulus ";
            }
            hasil += gabungKode(belum[i].belumLulus) + "\n";
        }
        return hasil;
    }

    public static String gabungKode(Matakuliah[] daftar) {
        String hasil = "";
        for (int i = 0; i < daftar.length; i++) {
            if (i > 0) {
                hasil += ", ";
            }
            hasil += daftar[i].kode;
        }
        return hasil;
    }
}