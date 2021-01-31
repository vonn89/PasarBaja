/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.excellentsystem.PasarBaja.DAO;

import com.excellentsystem.PasarBaja.Model.BebanPenjualanDetail;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Xtreme
 */
public class BebanPenjualanDetailDAO {
    public static List<BebanPenjualanDetail> getAllByDateAndStatus(Connection con, String tglMulai,String tglAkhir, String status)throws Exception{
        String sql = "select * from tt_beban_penjualan_detail "
            + " where no_beban_penjualan in (select no_beban_penjualan from tt_beban_penjualan_head "
            + " where left(tgl_beban_penjualan,10) between ? and ? ";
        if(!status.equals("%"))
            sql = sql + " and status = '"+status+"'";
        sql = sql + " )";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, tglMulai);
        ps.setString(2, tglAkhir);
        ResultSet rs = ps.executeQuery();
        List<BebanPenjualanDetail> allBeban = new ArrayList<>();
        while(rs.next()){
            BebanPenjualanDetail b = new BebanPenjualanDetail();
            b.setNoBebanPenjualan(rs.getString(1));
            b.setNoPenjualan(rs.getString(2));
            b.setJumlahRp(rs.getDouble(3));
            b.setStatus(rs.getString(4));
            allBeban.add(b);
        }
        return allBeban;
    }
    public static List<BebanPenjualanDetail> getAllByNoBeban(Connection con, String noBeban)throws Exception{
        PreparedStatement ps = con.prepareStatement("select * from tt_beban_penjualan_detail where no_beban_penjualan = ?");
        ps.setString(1, noBeban);
        ResultSet rs = ps.executeQuery();
        List<BebanPenjualanDetail> allBeban = new ArrayList<>();
        while(rs.next()){
            BebanPenjualanDetail b = new BebanPenjualanDetail();
            b.setNoBebanPenjualan(rs.getString(1));
            b.setNoPenjualan(rs.getString(2));
            b.setJumlahRp(rs.getDouble(3));
            b.setStatus(rs.getString(4));
            allBeban.add(b);
        }
        return allBeban;
    }
    public static List<BebanPenjualanDetail> getAllNoPenjualanAndStatus(Connection con, String noPenjualan, String status)throws Exception{
        PreparedStatement ps = con.prepareStatement("select * from tt_beban_penjualan_detail where no_penjualan = ? and status = ?");
        ps.setString(1, noPenjualan);
        ps.setString(2, status);
        ResultSet rs = ps.executeQuery();
        List<BebanPenjualanDetail> allBeban = new ArrayList<>();
        while(rs.next()){
            BebanPenjualanDetail b = new BebanPenjualanDetail();
            b.setNoBebanPenjualan(rs.getString(1));
            b.setNoPenjualan(rs.getString(2));
            b.setJumlahRp(rs.getDouble(3));
            b.setStatus(rs.getString(4));
            allBeban.add(b);
        }
        return allBeban;
    }
    public static void insert(Connection con, BebanPenjualanDetail b)throws Exception{
        PreparedStatement ps = con.prepareStatement("insert into tt_beban_penjualan_detail values (?,?,?,?)");
        ps.setString(1, b.getNoBebanPenjualan());
        ps.setString(2, b.getNoPenjualan());
        ps.setDouble(3, b.getJumlahRp());
        ps.setString(4, b.getStatus());
        ps.executeUpdate();
    }
    public static void update(Connection con, BebanPenjualanDetail b)throws Exception{
        PreparedStatement ps = con.prepareStatement("update tt_beban_penjualan_detail set "
                + " jumlah_rp = ?, status = ? where no_beban_penjualan = ? and no_penjualan = ?");
        ps.setDouble(1, b.getJumlahRp());
        ps.setString(2, b.getStatus());
        ps.setString(3, b.getNoBebanPenjualan());
        ps.setString(4, b.getNoPenjualan());
        ps.executeUpdate();
    }
}
