/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.excellentsystem.PasarBaja.DAO;

import static com.excellentsystem.PasarBaja.Main.yymm;
import com.excellentsystem.PasarBaja.Model.AsetTetap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Xtreme
 */
public class AsetTetapDAO {
    public static List<AsetTetap> getAllByStatus(Connection con, String status)throws Exception{
        String sql = "select * from tm_aset_tetap ";
        if(!status.equals("%"))
            sql = sql + " where status = '"+status+"' ";
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        List<AsetTetap> allAsetTetap = new ArrayList<>();
        while(rs.next()){
            AsetTetap a = new AsetTetap();
            a.setNoAset(rs.getString(1));
            a.setNama(rs.getString(2));
            a.setKategori(rs.getString(3));
            a.setKeterangan(rs.getString(4));
            a.setMasaPakai(rs.getInt(5));
            a.setNilaiAwal(rs.getDouble(6));
            a.setPenyusutan(rs.getDouble(7));
            a.setNilaiAkhir(rs.getDouble(8));
            a.setHargaJual(rs.getDouble(9));
            a.setStatus(rs.getString(10));
            a.setTglJual(rs.getDate(11).toString()+" "+rs.getTime(11).toString());
            a.setUserJual(rs.getString(12));
            a.setTglBeli(rs.getDate(13).toString()+" "+rs.getTime(13).toString());
            a.setUserBeli(rs.getString(14));
            allAsetTetap.add(a);
        }
        return allAsetTetap;
    }
    public static String getId(Connection con, Date date)throws Exception{
        PreparedStatement ps = con.prepareStatement("select max(right(no_aset_tetap,3)) from tm_aset_tetap "
                + "where mid(no_aset_tetap,4,4) = ?");
        ps.setString(1, yymm.format(date));
        ResultSet rs = ps.executeQuery();
        if(rs.next())
            return "AT-"+yymm.format(date)+new DecimalFormat("000").format(rs.getInt(1)+1);
        else
            return "AT-"+yymm.format(date)+new DecimalFormat("000").format(1);
    }
    public static void insert(Connection con, AsetTetap a)throws Exception{
        PreparedStatement ps = con.prepareStatement("insert into tm_aset_tetap values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        ps.setString(1, a.getNoAset());
        ps.setString(2, a.getNama());
        ps.setString(3, a.getKategori());
        ps.setString(4, a.getKeterangan());
        ps.setInt(5, a.getMasaPakai());
        ps.setDouble(6, a.getNilaiAwal());
        ps.setDouble(7, a.getPenyusutan());
        ps.setDouble(8, a.getNilaiAkhir());
        ps.setDouble(9, a.getHargaJual());
        ps.setString(10, a.getStatus());
        ps.setString(11, a.getTglJual());
        ps.setString(12, a.getUserJual());
        ps.setString(13, a.getTglBeli());
        ps.setString(14, a.getUserBeli());
        ps.executeUpdate();
    }
    public static void update(Connection con, AsetTetap a)throws Exception{
        PreparedStatement ps = con.prepareStatement("update tm_aset_tetap set nama=?, kategori=?, keterangan=?, masa_pakai=?,"
            + " nilai_awal=?, penyusutan=?, nilai_akhir=?, harga_jual=?, status=?, tgl_jual=?, user_jual=?, tgl_beli=?,"
            + " user_beli=? where no_aset_tetap=? ");
        ps.setString(1, a.getNama());
        ps.setString(2, a.getKategori());
        ps.setString(3, a.getKeterangan());
        ps.setInt(4, a.getMasaPakai());
        ps.setDouble(5, a.getNilaiAwal());
        ps.setDouble(6, a.getPenyusutan());
        ps.setDouble(7, a.getNilaiAkhir());
        ps.setDouble(8, a.getHargaJual());
        ps.setString(9, a.getStatus());
        ps.setString(10, a.getTglJual());
        ps.setString(11, a.getUserJual());
        ps.setString(12, a.getTglBeli());
        ps.setString(13, a.getUserBeli());
        ps.setString(14, a.getNoAset());
        ps.executeUpdate();
    }
}
