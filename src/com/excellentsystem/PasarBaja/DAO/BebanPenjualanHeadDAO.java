/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.excellentsystem.PasarBaja.DAO;

import static com.excellentsystem.PasarBaja.Main.yymm;
import com.excellentsystem.PasarBaja.Model.BebanPenjualanHead;
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
public class BebanPenjualanHeadDAO {
    public static List<BebanPenjualanHead> getAllByDateAndStatus(Connection con, String tglMulai,String tglAkhir, String status)throws Exception{
        String sql = "select * from tt_beban_penjualan_head "
            + " where left(tgl_beban_penjualan,10) between ? and ? ";
        if(!status.equals("%"))
            sql = sql + " and status = '"+status+"'";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, tglMulai);
        ps.setString(2, tglAkhir);
        ResultSet rs = ps.executeQuery();
        List<BebanPenjualanHead> allBeban = new ArrayList<>();
        while(rs.next()){
            BebanPenjualanHead b = new BebanPenjualanHead();
            b.setNoBebanPenjualan(rs.getString(1));
            b.setTglBebanPenjualan(rs.getString(2));
            b.setKeterangan(rs.getString(3));
            b.setTotalBebanPenjualan(rs.getDouble(4));
            b.setTipeKeuangan(rs.getString(5));
            b.setKodeUser(rs.getString(6));
            b.setTglBatal(rs.getString(7));
            b.setUserBatal(rs.getString(8));
            b.setStatus(rs.getString(9));
            allBeban.add(b);
        }
        return allBeban;
    }
    public static BebanPenjualanHead get(Connection con, String noBeban)throws Exception{
        PreparedStatement ps = con.prepareStatement("select * from tt_beban_penjualan_head where no_beban_penjualan = ?");
        ps.setString(1, noBeban);
        ResultSet rs = ps.executeQuery();
        BebanPenjualanHead b = null;
        while(rs.next()){
            b = new BebanPenjualanHead();
            b.setNoBebanPenjualan(rs.getString(1));
            b.setTglBebanPenjualan(rs.getString(2));
            b.setKeterangan(rs.getString(3));
            b.setTotalBebanPenjualan(rs.getDouble(4));
            b.setTipeKeuangan(rs.getString(5));
            b.setKodeUser(rs.getString(6));
            b.setTglBatal(rs.getString(7));
            b.setUserBatal(rs.getString(8));
            b.setStatus(rs.getString(9));
        }
        return b;
    }
    public static String getId(Connection con, Date date)throws Exception{
        PreparedStatement ps = con.prepareStatement("select max(right(no_beban_penjualan,3)) from tt_beban_penjualan_head "
                + " where mid(no_beban_penjualan,4,4) = ?");
        ps.setString(1, yymm.format(date));
        ResultSet rs = ps.executeQuery();
        if(rs.next())
            return "BJ-"+yymm.format(date)+new DecimalFormat("000").format(rs.getInt(1)+1);
        else
            return "BJ-"+yymm.format(date)+new DecimalFormat("000").format(1);
    }
    public static void insert(Connection con, BebanPenjualanHead b)throws Exception{
        PreparedStatement ps = con.prepareStatement("insert into tt_beban_penjualan_head values (?,?,?,?,?,?,?,?,?)");
        ps.setString(1, b.getNoBebanPenjualan());
        ps.setString(2, b.getTglBebanPenjualan());
        ps.setString(3, b.getKeterangan());
        ps.setDouble(4, b.getTotalBebanPenjualan());
        ps.setString(5, b.getTipeKeuangan());
        ps.setString(6, b.getKodeUser());
        ps.setString(7, b.getTglBatal());
        ps.setString(8, b.getUserBatal());
        ps.setString(9, b.getStatus());
        ps.executeUpdate();
    }
    public static void update(Connection con, BebanPenjualanHead b)throws Exception{
        PreparedStatement ps = con.prepareStatement("update tt_beban_penjualan_head set "
                + " tgl_beban_penjualan = ?, keterangan = ?, total_beban_penjualan = ?, tipe_keuangan = ?, "
                + " kode_user = ?, tgl_batal = ?, user_batal = ?, status = ? where no_beban_penjualan = ?");
        ps.setString(1, b.getTglBebanPenjualan());
        ps.setString(2, b.getKeterangan());
        ps.setDouble(3, b.getTotalBebanPenjualan());
        ps.setString(4, b.getTipeKeuangan());
        ps.setString(5, b.getKodeUser());
        ps.setString(6, b.getTglBatal());
        ps.setString(7, b.getUserBatal());
        ps.setString(8, b.getStatus());
        ps.setString(9, b.getNoBebanPenjualan());
        ps.executeUpdate();
    }
}
