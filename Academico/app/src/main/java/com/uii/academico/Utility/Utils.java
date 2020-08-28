package com.uii.academico.Utility;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

public class Utils {

    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editSP;
    private static final String sharedPreferenceID = "temp";



    public static String TOKEN = "";
    public static String KIRIMPESANPERSONAL = "http://academicouii.web.id/kirimPesanPersonal.php";
    public static String KIRIMPESANGROUP = "http://academicouii.web.id/kirimPesanGroup.php";
    public static String LIHATPESAN = "http://academicouii.web.id/lihatPesan.php";
    public static String LIHATPESANGROUP = "http://academicouii.web.id/lihatPesanGroup.php";
    public static String LIHATMEMBER = "http://academicouii.web.id/lihatMemberGroup.php";

    public static String LOGIN = "http://academicouii.web.id/login.php/";
    public static String LOGOUT = "http://academicouii.web.id/logout.php";

    public static String INFORMASI = "http://academicouii.web.id/informasi.php";
    public static String KONTAK = "http://academicouii.web.id/kontak.php";
    public static String OBROLAN = "http://academicouii.web.id/obrolan.php";

    public static String BIMBINGAN = "http://academicouii.web.id/mhsBimbingan.php";

    public static String BUATJADWAL = "http://academicouii.web.id/buatJadwal.php";
    public static String RIWAYATBIMBINGAN = "http://academicouii.web.id/riwayatBimbingan.php";
    public static String KONFIRMASIBIMBINGAN = "http://academicouii.web.id/konfirmasiBimbingan.php";
    public static String KONFIRMASITOPIKBIMBINGAN = "http://academicouii.web.id/konfirmasiTopikBimbingan.php";
    public static String KONFIRMASIHASILBIMBINGAN = "http://academicouii.web.id/konfirmasiHasilBimbingan.php";

    public static String BUATDATAAKADEMIK = "http://academicouii.web.id/buatDataAkademik.php";
    public static String DATAAKADEMIK = "http://academicouii.web.id/dataAkademik.php";
    public static String VALIDASIDATAAKADEMIK = "http://academicouii.web.id/validasiDataAkademik.php";

    public static String UPLOADINFORMASI = "http://academicouii.web.id/postInformasi.php";
    public static String PROFILEUPDATE = "http://academicouii.web.id/profil_update.php";

    public static String HAPUSDATAAKADEMIK = "http://academicouii.web.id/hapusDataAkademik.php";
    public static String HAPUSINFORMASI = "http://academicouii.web.id/hapusInformasi.php";
    public static String HAPUSRIWAYATBIMBINGAN = "http://academicouii.web.id/hapusRiwayatBimbingan.php";

    public static String LIHATDAFTARBIMBINGAN = "http://academicouii.web.id/lihatbimbinganmahasiswa.php";
    public static String PILIHANJADWALTATAPMUKA = "http://academicouii.web.id/pilihanJadwalTatapMuka.php";
    public static String RIWAYATTATAPMUKA = "http://academicouii.web.id/riwayatTatapMuka.php";

    public static void init(Context context){
        sharedPreferences = context.getSharedPreferences(sharedPreferenceID, Context.MODE_PRIVATE);
        editSP = sharedPreferences.edit();
    }

    public static void clearPreference(){
        editSP.clear();
        editSP.commit();
    }
    

}
