/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.excellentsystem.PasarBaja.Services;

import com.excellentsystem.PasarBaja.DAO.AsetTetapDAO;
import com.excellentsystem.PasarBaja.DAO.BarangDAO;
import com.excellentsystem.PasarBaja.DAO.BebanPembelianDAO;
import com.excellentsystem.PasarBaja.DAO.BebanPenjualanDetailDAO;
import com.excellentsystem.PasarBaja.DAO.BebanPenjualanHeadDAO;
import com.excellentsystem.PasarBaja.DAO.CustomerDAO;
import com.excellentsystem.PasarBaja.DAO.HutangDAO;
import com.excellentsystem.PasarBaja.DAO.KategoriHutangDAO;
import com.excellentsystem.PasarBaja.DAO.KategoriKeuanganDAO;
import com.excellentsystem.PasarBaja.DAO.KategoriPiutangDAO;
import com.excellentsystem.PasarBaja.DAO.KategoriTransaksiDAO;
import com.excellentsystem.PasarBaja.DAO.KeuanganDAO;
import com.excellentsystem.PasarBaja.DAO.LogBarangDAO;
import com.excellentsystem.PasarBaja.DAO.OtoritasDAO;
import com.excellentsystem.PasarBaja.DAO.PembayaranDAO;
import com.excellentsystem.PasarBaja.DAO.PembelianDetailDAO;
import com.excellentsystem.PasarBaja.DAO.PembelianHeadDAO;
import com.excellentsystem.PasarBaja.DAO.PemesananDetailDAO;
import com.excellentsystem.PasarBaja.DAO.PemesananHeadDAO;
import com.excellentsystem.PasarBaja.DAO.PenjualanDetailDAO;
import com.excellentsystem.PasarBaja.DAO.PenjualanHeadDAO;
import com.excellentsystem.PasarBaja.DAO.PenyesuaianStokBarangDAO;
import com.excellentsystem.PasarBaja.DAO.PiutangDAO;
import com.excellentsystem.PasarBaja.DAO.StokBarangDAO;
import com.excellentsystem.PasarBaja.DAO.SupplierDAO;
import com.excellentsystem.PasarBaja.DAO.TerimaPembayaranDAO;
import com.excellentsystem.PasarBaja.DAO.UserDAO;
import com.excellentsystem.PasarBaja.Function;
import static com.excellentsystem.PasarBaja.Main.sistem;
import static com.excellentsystem.PasarBaja.Main.tglBarang;
import static com.excellentsystem.PasarBaja.Main.tglSql;
import com.excellentsystem.PasarBaja.Model.AsetTetap;
import com.excellentsystem.PasarBaja.Model.Barang;
import com.excellentsystem.PasarBaja.Model.BebanPembelian;
import com.excellentsystem.PasarBaja.Model.BebanPenjualanDetail;
import com.excellentsystem.PasarBaja.Model.BebanPenjualanHead;
import com.excellentsystem.PasarBaja.Model.Customer;
import com.excellentsystem.PasarBaja.Model.Hutang;
import com.excellentsystem.PasarBaja.Model.KategoriHutang;
import com.excellentsystem.PasarBaja.Model.KategoriKeuangan;
import com.excellentsystem.PasarBaja.Model.KategoriPiutang;
import com.excellentsystem.PasarBaja.Model.KategoriTransaksi;
import com.excellentsystem.PasarBaja.Model.Keuangan;
import com.excellentsystem.PasarBaja.Model.LogBarang;
import com.excellentsystem.PasarBaja.Model.Otoritas;
import com.excellentsystem.PasarBaja.Model.Pembayaran;
import com.excellentsystem.PasarBaja.Model.PembelianDetail;
import com.excellentsystem.PasarBaja.Model.PembelianHead;
import com.excellentsystem.PasarBaja.Model.PemesananDetail;
import com.excellentsystem.PasarBaja.Model.PemesananHead;
import com.excellentsystem.PasarBaja.Model.PenjualanDetail;
import com.excellentsystem.PasarBaja.Model.PenjualanHead;
import com.excellentsystem.PasarBaja.Model.PenyesuaianStokBarang;
import com.excellentsystem.PasarBaja.Model.Piutang;
import com.excellentsystem.PasarBaja.Model.StokBarang;
import com.excellentsystem.PasarBaja.Model.Supplier;
import com.excellentsystem.PasarBaja.Model.TerimaPembayaran;
import com.excellentsystem.PasarBaja.Model.User;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Xtreme
 */
public class Service {

    private static void insertKeuangan(Connection con, String noKeuangan, String tanggal, String tipeKeuangan,
            String kategori, String deskripsi, double jumlahRp, String kodeUser) throws Exception {
        Keuangan k = new Keuangan();
        k.setNoKeuangan(noKeuangan);
        k.setTglKeuangan(tanggal);
        k.setTipeKeuangan(tipeKeuangan);
        k.setKategori(kategori);
        k.setDeskripsi(deskripsi);
        k.setJumlahRp(jumlahRp);
        k.setKodeUser(kodeUser);
        KeuanganDAO.insert(con, k);
    }

    public static String setPenyusutanAset(Connection con) {
        try {
            con.setAutoCommit(false);
            String status = "true";

            LocalDate now = LocalDate.parse(tglBarang.format(Function.getServerDate(con)), DateTimeFormatter.ISO_DATE);
            List<Keuangan> listKeuanganAsetTetap = KeuanganDAO.getAllByTipeKeuangan(con, "Aset Tetap");
            for (AsetTetap aset : AsetTetapDAO.getAllByStatus(con, "open")) {
                if (aset.getMasaPakai() != 0) {
                    LocalDate tglBeli = LocalDate.parse(aset.getTglBeli(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    int selisih = ((now.getYear() - tglBeli.getYear()) * 12) + (now.getMonthValue() - tglBeli.getMonthValue());
                    if (selisih <= aset.getMasaPakai()) {
                        double totalPenyusutan = 0;
                        double penyusutanPerbulan = aset.getNilaiAwal() / aset.getMasaPakai();
                        for (int i = 1; i <= selisih; i++) {
                            LocalDate tglSusut = tglBeli.plusMonths(i);
                            if (tglSusut.isBefore(now) || tglSusut.isEqual(now)) {
                                boolean statusInput = true;
                                for (Keuangan k : listKeuanganAsetTetap) {
                                    if (k.getDeskripsi().equals("Penyusutan Aset Tetap Ke-" + i + " (" + aset.getNoAset() + ")")) {
                                        statusInput = false;
                                    }
                                }
                                if (statusInput) {
                                    Date date = tglBarang.parse(tglSusut.toString());
                                    String noKeuangan = KeuanganDAO.getId(con, date);
                                    insertKeuangan(con, noKeuangan, tglSusut.toString() + " 00:00:00", "Aset Tetap", aset.getKategori(),
                                            "Penyusutan Aset Tetap Ke-" + i + " (" + aset.getNoAset() + ")", -penyusutanPerbulan, "System");
                                    insertKeuangan(con, noKeuangan, tglSusut.toString() + " 00:00:00", "Pendapatan/Beban", "Beban Penyusutan Aset Tetap",
                                            "Penyusutan Aset Tetap Ke-" + i + " (" + aset.getNoAset() + ")", -penyusutanPerbulan, "System");
                                }
                                totalPenyusutan = totalPenyusutan + penyusutanPerbulan;
                            }
                        }
                        aset.setPenyusutan(totalPenyusutan);
                        aset.setNilaiAkhir(aset.getNilaiAwal() - totalPenyusutan);
                        AsetTetapDAO.update(con, aset);
                    }
                }
            }

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);
            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String newBarang(Connection con, Barang barang) {
        try {
            con.setAutoCommit(false);
            String status = "true";

            if (BarangDAO.get(con, barang.getKodeBarang()) != null) {
                status = "Kode barang sudah pernah terdaftar";
            } else {
                BarangDAO.insert(con, barang);

                Date now = Function.getServerDate(con);
                
                LogBarang log = new LogBarang();
                log.setTanggal(tglSql.format(now));
                log.setKodeBarang(barang.getKodeBarang());
                log.setKategori("New Barang");
                log.setKeterangan("");
                log.setStokAwal(0);
                log.setNilaiAwal(0);
                log.setStokMasuk(0);
                log.setNilaiMasuk(0);
                log.setStokKeluar(0);
                log.setNilaiKeluar(0);
                log.setStokAkhir(0);
                log.setNilaiAkhir(0);
                LogBarangDAO.insert(con, log);

                StokBarang stok = new StokBarang();
                stok.setTanggal(tglBarang.format(now));
                stok.setKodeBarang(barang.getKodeBarang());
                stok.setStokAwal(0);
                stok.setStokMasuk(0);
                stok.setStokKeluar(0);
                stok.setStokAkhir(0);
                StokBarangDAO.insert(con, stok);
            }

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);
            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String updateBarang(Connection con, Barang barang) {
        try {
            con.setAutoCommit(false);
            String status = "true";

            BarangDAO.update(con, barang);

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);
            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String deleteBarang(Connection con, Barang barang) {
        try {
            con.setAutoCommit(false);
            String status = "true";

            double stokAkhir = 0;
            LogBarang log = LogBarangDAO.getLastByBarang(con, barang.getKodeBarang());
            stokAkhir = stokAkhir + log.getStokAkhir();
            if (stokAkhir > 0) {
                status = "Barang tidak dapat dihapus, karena stok barang masih ada";
            } else {
                barang.setStatus("false");
                BarangDAO.update(con, barang);
            }

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);
            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String newCustomer(Connection con, Customer customer) {
        try {
            con.setAutoCommit(false);
            String status = "true";

            CustomerDAO.insert(con, customer);

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);
            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String updateCustomer(Connection con, Customer customer) {
        try {
            con.setAutoCommit(false);
            String status = "true";

            CustomerDAO.update(con, customer);

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);
            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String deleteCustomer(Connection con, Customer customer) {
        try {
            con.setAutoCommit(false);
            String status = "true";

            customer.setStatus("false");
            CustomerDAO.update(con, customer);

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);
            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String newSupplier(Connection con, Supplier supplier) {
        try {
            con.setAutoCommit(false);
            String status = "true";

            SupplierDAO.insert(con, supplier);

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);
            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String updateSupplier(Connection con, Supplier supplier) {
        try {
            con.setAutoCommit(false);
            String status = "true";

            SupplierDAO.update(con, supplier);

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);
            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String deleteSupplier(Connection con, Supplier supplier) {
        try {
            con.setAutoCommit(false);
            String status = "true";

            supplier.setStatus("false");
            SupplierDAO.update(con, supplier);

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);
            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String newUser(Connection con, User user) {
        try {
            String status = "true";
            con.setAutoCommit(false);

            for (User u : UserDAO.getAll(con)) {
                if (user.getKodeUser().equals(u.getKodeUser())) {
                    status = "Username sudah pernah terdaftar";
                }
            }
            UserDAO.insert(con, user);
            for (Otoritas o : user.getOtoritas()) {
                OtoritasDAO.insert(con, o);
            }

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);
            return "true";
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String updateUser(Connection con, User user) {
        try {
            con.setAutoCommit(false);

            UserDAO.update(con, user);
            OtoritasDAO.delete(con, user);
            for (Otoritas o : user.getOtoritas()) {
                OtoritasDAO.insert(con, o);
            }

            con.commit();
            con.setAutoCommit(true);
            return "true";
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String deleteUser(Connection con, User user) {
        try {
            con.setAutoCommit(false);

            user.setStatus("false");
            UserDAO.update(con, user);
            OtoritasDAO.delete(con, user);

            con.commit();
            con.setAutoCommit(true);
            return "true";
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String newKategoriKeuangan(Connection con, KategoriKeuangan t) throws Exception {
        try {
            String status = "true";
            con.setAutoCommit(false);

            KategoriKeuanganDAO.insert(con, t);

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);
            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String deleteKategoriKeuangan(Connection con, KategoriKeuangan t) throws Exception {
        try {
            String status = "true";
            con.setAutoCommit(false);

            double saldo = KeuanganDAO.getSaldoAkhir(con, tglBarang.format(Function.getServerDate(con)), t.getKodeKeuangan());
            if (saldo != 0) {
                status = "Tidak dapat dihapus,karena saldo " + t.getKodeKeuangan() + " masih ada";
            } else {
                KategoriKeuanganDAO.delete(con, t);
            }

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);
            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String newKategoriTransaksi(Connection con, KategoriTransaksi k) throws Exception {
        try {
            String status = "true";
            con.setAutoCommit(false);

            KategoriTransaksiDAO.insert(con, k);

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);
            return status;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String updateKategoriTransaksi(Connection con, KategoriTransaksi k) throws Exception {
        try {
            String status = "true";
            con.setAutoCommit(false);

            KategoriTransaksiDAO.update(con, k);

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);
            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String deleteKategoriTransaksi(Connection con, KategoriTransaksi k) throws Exception {
        try {
            String status = "true";
            con.setAutoCommit(false);

            k.setStatus("false");
            KategoriTransaksiDAO.update(con, k);

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);
            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String newKategoriHutang(Connection con, KategoriHutang k) throws Exception {
        try {
            String status = "true";
            con.setAutoCommit(false);

            KategoriHutangDAO.insert(con, k);

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);
            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String deleteKategoriHutang(Connection con, KategoriHutang k) throws Exception {
        try {
            String status = "true";
            con.setAutoCommit(false);

            KategoriHutangDAO.delete(con, k);

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);
            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String newKategoriPiutang(Connection con, KategoriPiutang k) throws Exception {
        try {
            String status = "true";
            con.setAutoCommit(false);

            KategoriPiutangDAO.insert(con, k);

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);
            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String deleteKategoriPiutang(Connection con, KategoriPiutang k) throws Exception {
        try {
            String status = "true";
            con.setAutoCommit(false);

            KategoriPiutangDAO.delete(con, k);

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);
            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String newPemesanan(Connection con, PemesananHead pemesanan) {
        try {
            con.setAutoCommit(false);
            String status = "true";

            Date date = Function.getServerDate(con);
            String noPemesanan = PemesananHeadDAO.getId(con, date);
            pemesanan.setNoPemesanan(noPemesanan);
            pemesanan.setTglPemesanan(tglSql.format(date));
            PemesananHeadDAO.insert(con, pemesanan);
            int noUrut = 1;
            for(PemesananDetail detail : pemesanan.getListPemesananDetail()){
                detail.setNoPemesanan(noPemesanan);
                detail.setNoUrut(noUrut);
                PemesananDetailDAO.insert(con, detail);
                
                noUrut = noUrut + 1;
            }

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);

            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String editPemesanan(Connection con, PemesananHead p) {
        try {
            con.setAutoCommit(false);
            String status = "true";

            PemesananHeadDAO.update(con, p);
            PemesananDetailDAO.delete(con, p.getNoPemesanan());
            for (PemesananDetail detail : p.getListPemesananDetail()) {
                PemesananDetailDAO.insert(con, detail);
            }

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);

            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String batalPemesanan(Connection con, PemesananHead pemesanan) {
        try {
            con.setAutoCommit(false);
            String status = "true";

            Date date = Function.getServerDate(con);
            pemesanan.setTglBatal(tglSql.format(date));
            pemesanan.setUserBatal(sistem.getUser().getKodeUser());
            pemesanan.setStatus("false");
            PemesananHeadDAO.update(con, pemesanan);

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);
            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String selesaiApprovePemesanan(Connection con, PemesananHead pemesanan) {
        try {
            con.setAutoCommit(false);
            String status = "true";

            PemesananHeadDAO.update(con, pemesanan);

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);

            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String newTerimaDownPayment(Connection con, PemesananHead pemesanan, double jumlahBayar, String tipeKeuangan) {
        try {
            con.setAutoCommit(false);
            String status = "true";

            Date date = Function.getServerDate(con);
            String noKeuangan = KeuanganDAO.getId(con, date);

            PemesananHeadDAO.update(con, pemesanan);

            Hutang h = new Hutang();
            h.setNoHutang(HutangDAO.getId(con, date));
            h.setTglHutang(tglSql.format(date));
            h.setKategori("Terima Pembayaran Down Payment");
            h.setKeterangan(pemesanan.getNoPemesanan());
            h.setTipeKeuangan(tipeKeuangan);
            h.setJumlahHutang(jumlahBayar);
            h.setPembayaran(0);
            h.setSisaHutang(jumlahBayar);
            h.setKodeUser(sistem.getUser().getKodeUser());
            h.setStatus("open");
            HutangDAO.insert(con, h);

            insertKeuangan(con, noKeuangan, tglSql.format(date), tipeKeuangan, "Terima Pembayaran Down Payment",
                    "Terima Pembayaran Down Payment - " + pemesanan.getNoPemesanan() + " (" + h.getNoHutang() + ")", jumlahBayar, sistem.getUser().getKodeUser());
            insertKeuangan(con, noKeuangan, tglSql.format(date), "Hutang", "Terima Pembayaran Down Payment",
                    "Terima Pembayaran Down Payment - " + pemesanan.getNoPemesanan() + " (" + h.getNoHutang() + ")", jumlahBayar, sistem.getUser().getKodeUser());

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);

            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String batalTerimaDownPayment(Connection con, Hutang h) {
        try {
            con.setAutoCommit(false);
            String status = "true";

            HutangDAO.delete(con, h);

            PemesananHead pemesanan = h.getPemesananHead();
            pemesanan.setDownPayment(pemesanan.getDownPayment() - h.getJumlahHutang());
            pemesanan.setSisaDownPayment(pemesanan.getSisaDownPayment() - h.getJumlahHutang());
            PemesananHeadDAO.update(con, pemesanan);

            KeuanganDAO.delete(con, h.getTipeKeuangan(), "Terima Pembayaran Down Payment",
                    "Terima Pembayaran Down Payment - " + pemesanan.getNoPemesanan() + " (" + h.getNoHutang() + ")");
            KeuanganDAO.delete(con, "Hutang", "Terima Pembayaran Down Payment",
                    "Terima Pembayaran Down Payment - " + pemesanan.getNoPemesanan() + " (" + h.getNoHutang() + ")");

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);

            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String newPenjualan(Connection con, PenjualanHead p) {
        try {
            con.setAutoCommit(false);
            String status = "true";

            Date date = Function.getServerDate(con);
            
            String noPenjualan = PenjualanHeadDAO.getId(con, date);
            p.setNoPenjualan(noPenjualan);
            p.setTglPenjualan(tglSql.format(date));
            PenjualanHeadDAO.insert(con, p);
            
            int noUrut = 1;
            for (PenjualanDetail d : p.getListPenjualanDetail()) {
                d.setNoPenjualan(noPenjualan);
                d.setNoUrut(noUrut);
                PenjualanDetailDAO.insert(con, d);
                
                noUrut++;
            }

            
            PemesananHead pemesanan = PemesananHeadDAO.get(con, p.getNoPemesanan());
            double dp = pemesanan.getSisaDownPayment();
            if (p.getTotalPenjualan() >= dp) {
                p.setPembayaran(dp);
            } else if (p.getTotalPenjualan() < dp) {
                p.setPembayaran(p.getTotalPenjualan());
            }
            p.setSisaPembayaran(p.getTotalPenjualan() - p.getPembayaran());

            String noKeuangan = KeuanganDAO.getId(con, date);

            PenjualanHeadDAO.update(con, p);

            insertKeuangan(con, noKeuangan, p.getTglPenjualan(), "Penjualan", "Penjualan",
                    "Penjualan - " + p.getNoPenjualan(), p.getTotalPenjualan(), sistem.getUser().getKodeUser());
            insertKeuangan(con, noKeuangan, p.getTglPenjualan(), "Piutang", "Piutang Penjualan",
                    "Penjualan - " + p.getNoPenjualan(), p.getSisaPembayaran(), sistem.getUser().getKodeUser());

            Piutang piutang = new Piutang();
            piutang.setNoPiutang(PiutangDAO.getId(con, date));
            piutang.setTglPiutang(p.getTglPenjualan());
            piutang.setKategori("Piutang Penjualan");
            piutang.setKeterangan(p.getNoPenjualan());
            piutang.setTipeKeuangan("Penjualan");
            piutang.setJumlahPiutang(p.getTotalPenjualan());
            piutang.setPembayaran(p.getPembayaran());
            piutang.setSisaPiutang(p.getSisaPembayaran());
            piutang.setKodeUser(sistem.getUser().getKodeUser());
            if (piutang.getSisaPiutang() > 0) {
                piutang.setStatus("open");
            } else {
                piutang.setStatus("close");
            }
            PiutangDAO.insert(con, piutang);


            if (p.getPembayaran() > 0) {
                pemesanan.setSisaDownPayment(pemesanan.getSisaDownPayment() - p.getPembayaran());

                TerimaPembayaran tp = new TerimaPembayaran();
                tp.setNoTerimaPembayaran(TerimaPembayaranDAO.getId(con, date));
                tp.setTglTerima(p.getTglPenjualan());
                tp.setNoPiutang(piutang.getNoPiutang());
                tp.setJumlahPembayaran(p.getPembayaran());
                tp.setTipeKeuangan("Down Payment");
                tp.setCatatan(p.getNoPemesanan());
                tp.setKodeUser(sistem.getUser().getKodeUser());
                tp.setTglBatal("2000-01-01 00:00:00");
                tp.setUserBatal("");
                tp.setStatus("true");
                TerimaPembayaranDAO.insert(con, tp);

                double bayar = p.getPembayaran();
                List<Hutang> listHutang = HutangDAO.getAllByKategoriAndKeteranganAndStatus(
                        con, "Terima Pembayaran Down Payment", pemesanan.getNoPemesanan(), "%");
                for (Hutang h : listHutang) {
                    if (h.getSisaHutang() > bayar) {
                        Pembayaran pembayaran = new Pembayaran();
                        pembayaran.setNoPembayaran(PembayaranDAO.getId(con, date));
                        pembayaran.setTglPembayaran(p.getTglPenjualan());
                        pembayaran.setNoHutang(h.getNoHutang());
                        pembayaran.setJumlahPembayaran(bayar);
                        pembayaran.setTipeKeuangan("Penjualan");
                        pembayaran.setCatatan(p.getNoPenjualan());
                        pembayaran.setKodeUser(sistem.getUser().getKodeUser());
                        pembayaran.setTglBatal("2000-01-01 00:00:00");
                        pembayaran.setUserBatal("");
                        pembayaran.setStatus("true");
                        PembayaranDAO.insert(con, pembayaran);

                        h.setPembayaran(h.getPembayaran() + bayar);
                        h.setSisaHutang(h.getSisaHutang() - bayar);
                        HutangDAO.update(con, h);

                        bayar = 0;
                    } else {
                        Pembayaran pembayaran = new Pembayaran();
                        pembayaran.setNoPembayaran(PembayaranDAO.getId(con, date));
                        pembayaran.setTglPembayaran(p.getTglPenjualan());
                        pembayaran.setNoHutang(h.getNoHutang());
                        pembayaran.setJumlahPembayaran(h.getSisaHutang());
                        pembayaran.setTipeKeuangan("Penjualan");
                        pembayaran.setCatatan(p.getNoPenjualan());
                        pembayaran.setKodeUser(sistem.getUser().getKodeUser());
                        pembayaran.setTglBatal("2000-01-01 00:00:00");
                        pembayaran.setUserBatal("");
                        pembayaran.setStatus("true");
                        PembayaranDAO.insert(con, pembayaran);

                        h.setPembayaran(h.getPembayaran() + h.getSisaHutang());
                        h.setSisaHutang(h.getSisaHutang() - h.getSisaHutang());
                        h.setStatus("close");
                        HutangDAO.update(con, h);

                        bayar = bayar - h.getSisaHutang();
                    }
                }

                insertKeuangan(con, noKeuangan, p.getTglPenjualan(), "Hutang", "Terima Pembayaran Down Payment",
                        "Penjualan - " + p.getNoPenjualan(), -p.getPembayaran(), sistem.getUser().getKodeUser());
            }
            
            for (PenjualanDetail detail : p.getListPenjualanDetail()) {
                PemesananDetail d = PemesananDetailDAO.get(con, detail.getNoPemesanan(), detail.getNoUrut());
                d.setQtyTerkirim(d.getQtyTerkirim() + detail.getQty());
                PemesananDetailDAO.update(con, d);
            }
            double qtyBelumDikirim = 0;
            List<PemesananDetail> listPemesanan = PemesananDetailDAO.getAllByNoPemesanan(con, pemesanan.getNoPemesanan());
            for (PemesananDetail d : listPemesanan) {
                qtyBelumDikirim = qtyBelumDikirim + d.getQty() - d.getQtyTerkirim();
            }
            if (qtyBelumDikirim == 0) {
                pemesanan.setStatus("close");
            }
            PemesananHeadDAO.update(con, pemesanan);

            List<PenjualanDetail> stokBarang = new ArrayList<>();
            double hpp = 0;
            for (PenjualanDetail d : p.getListPenjualanDetail()) {
                LogBarang log = LogBarangDAO.getLastByBarang(con, d.getKodeBarang());
                if (log.getStokAkhir() != 0) {
                    d.setNilai(log.getNilaiAkhir() / log.getStokAkhir());
                }
                PenjualanDetailDAO.update(con, d);

                hpp = hpp + d.getNilai() * d.getQty();

                Boolean inStok = false;
                for (PenjualanDetail detail : stokBarang) {
                    if (d.getKodeBarang().equals(detail.getKodeBarang())) {
                        detail.setNilai((detail.getNilai() * detail.getQty() + d.getNilai() * d.getQty())
                                / (detail.getQty() + d.getQty()));
                        detail.setQty(detail.getQty() + d.getQty());
                        inStok = true;
                    }
                }
                if (!inStok) {
                    PenjualanDetail temp = new PenjualanDetail();
                    temp.setNoPenjualan(d.getNoPenjualan());
                    temp.setNoPemesanan(d.getNoPemesanan());
                    temp.setNoUrut(d.getNoUrut());
                    temp.setKodeBarang(d.getKodeBarang());
                    temp.setNamaBarang(d.getNamaBarang());
                    temp.setKeterangan(d.getKeterangan());
                    temp.setSatuan(d.getSatuan());
                    temp.setQty(d.getQty());
                    temp.setNilai(d.getNilai());
                    temp.setHargaJual(d.getHargaJual());
                    temp.setTotal(d.getTotal());
                    stokBarang.add(temp);
                }
            }
            insertKeuangan(con, noKeuangan, p.getTglPenjualan(), "HPP", "Penjualan",
                    "Penjualan - " + p.getNoPenjualan(), hpp, sistem.getUser().getKodeUser());
            insertKeuangan(con, noKeuangan, p.getTglPenjualan(), "Stok Barang", "Stok Barang",
                    "Penjualan - " + p.getNoPenjualan(), -hpp, sistem.getUser().getKodeUser());

            for (PenjualanDetail d : stokBarang) {
                status = insertStokAndLogBarang(con, date, d.getKodeBarang(), "Penjualan", p.getNoPenjualan(),
                        0, 0, d.getQty(), (d.getNilai() * d.getQty()), status);
            }

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);

            return status;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String batalPenjualan(Connection con, PenjualanHead penjualan) {
        try {
            con.setAutoCommit(false);
            String status = "true";

            Date date = Function.getServerDate(con);
            
            penjualan.setTglBatal(tglSql.format(date));
            penjualan.setUserBatal(sistem.getUser().getKodeUser());
            penjualan.setStatus("false");
            PenjualanHeadDAO.update(con, penjualan);

            KeuanganDAO.delete(con, "Penjualan", "Penjualan", "Penjualan - " + penjualan.getNoPenjualan());

            Piutang piutang = PiutangDAO.getByKategoriAndKeteranganAndStatus(
                    con, "Piutang Penjualan", penjualan.getNoPenjualan(), "%");
            PiutangDAO.delete(con, piutang);

            KeuanganDAO.delete(con, "Piutang", "Piutang Penjualan", "Penjualan - " + penjualan.getNoPenjualan());

            PemesananHead pemesanan = PemesananHeadDAO.get(con, penjualan.getNoPemesanan());
            TerimaPembayaran dp = null;
            List<TerimaPembayaran> terimaPembayaran = TerimaPembayaranDAO.getAllByNoPiutangAndStatus(
                    con, piutang.getNoPiutang(), "true");
            for (TerimaPembayaran tp : terimaPembayaran) {
                if (tp.getTipeKeuangan().equals("Down Payment")) {
                    dp = tp;
                } else {
                    status = "Tidak dapat dibatal,karena sudah ada pembayaran";
                }
            }
            if (dp != null) {
                pemesanan.setSisaDownPayment(pemesanan.getSisaDownPayment() + dp.getJumlahPembayaran());

                dp.setTglBatal(tglSql.format(date));
                dp.setUserBatal(sistem.getUser().getKodeUser());
                dp.setStatus("false");
                TerimaPembayaranDAO.update(con, dp);

                List<Hutang> listHutang = HutangDAO.getAllByKategoriAndKeteranganAndStatus(
                        con, "Terima Pembayaran Down Payment", pemesanan.getNoPemesanan(), "%");
                for (Hutang h : listHutang) {
                    List<Pembayaran> pembayaran = PembayaranDAO.getAllByNoHutang(con, h.getNoHutang(), "true");
                    for (Pembayaran p : pembayaran) {
                        if (p.getTipeKeuangan().equals("Penjualan") && p.getCatatan().equals(penjualan.getNoPenjualan())) {
                            p.setTglBatal(tglSql.format(date));
                            p.setUserBatal(sistem.getUser().getKodeUser());
                            p.setStatus("false");
                            PembayaranDAO.update(con, p);

                            h.setPembayaran(h.getPembayaran() - p.getJumlahPembayaran());
                            h.setSisaHutang(h.getSisaHutang() + p.getJumlahPembayaran());
                            h.setStatus("open");
                            HutangDAO.update(con, h);
                        }
                    }
                }
                KeuanganDAO.delete(con, "Hutang", "Terima Pembayaran Down Payment", "Penjualan - " + penjualan.getNoPenjualan());
            }

            penjualan.setListPenjualanDetail(PenjualanDetailDAO.getAllPenjualanDetail(con, penjualan.getNoPenjualan()));
            for (PenjualanDetail detail : penjualan.getListPenjualanDetail()) {
                PemesananDetail d = PemesananDetailDAO.get(con, detail.getNoPemesanan(), detail.getNoUrut());
                d.setQtyTerkirim(d.getQtyTerkirim() - detail.getQty());
                PemesananDetailDAO.update(con, d);
            }
            pemesanan.setStatus("open");
            PemesananHeadDAO.update(con, pemesanan);

            List<PenjualanDetail> stokBarang = new ArrayList<>();
            double hpp = 0;
            for (PenjualanDetail d : penjualan.getListPenjualanDetail()) {
                hpp = hpp + d.getNilai() * d.getQty();

                Boolean inStok = false;
                for (PenjualanDetail detail : stokBarang) {
                    if (d.getKodeBarang().equals(detail.getKodeBarang())) {
                        detail.setNilai((detail.getNilai() * detail.getQty() + d.getNilai() * d.getQty())
                                / (detail.getQty() + d.getQty()));
                        detail.setQty(detail.getQty() + d.getQty());
                        inStok = true;
                    }
                }
                if (!inStok) {
                    PenjualanDetail temp = new PenjualanDetail();
                    temp.setNoPenjualan(d.getNoPenjualan());
                    temp.setNoPemesanan(d.getNoPemesanan());
                    temp.setNoUrut(d.getNoUrut());
                    temp.setKodeBarang(d.getKodeBarang());
                    temp.setNamaBarang(d.getNamaBarang());
                    temp.setKeterangan(d.getKeterangan());
                    temp.setSatuan(d.getSatuan());
                    temp.setQty(d.getQty());
                    temp.setNilai(d.getNilai());
                    temp.setHargaJual(d.getHargaJual());
                    temp.setTotal(d.getTotal());
                    stokBarang.add(temp);
                }
            }
            KeuanganDAO.delete(con, "HPP", "Penjualan", "Penjualan - " + penjualan.getNoPenjualan());

            KeuanganDAO.delete(con, "Stok Barang", "Stok Barang", "Penjualan - " + penjualan.getNoPenjualan());

            for (PenjualanDetail d : stokBarang) {
                StokBarang stok = StokBarangDAO.get(con, tglBarang.format(tglSql.parse(penjualan.getTglPenjualan())), d.getKodeBarang());
                stok.setStokKeluar(stok.getStokKeluar() - d.getQty());
                stok.setStokAkhir(stok.getStokAkhir() + d.getQty());
                StokBarangDAO.update(con, stok);

                LogBarangDAO.delete(con, d.getKodeBarang(), "Penjualan", penjualan.getNoPenjualan());

                resetStokDanLogBarang(con, d.getKodeBarang(),  penjualan.getTglPenjualan(), date);
            }

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);
            return status;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String newPembelian(Connection con, PembelianHead p) {
        try {
            con.setAutoCommit(false);
            String status = "true";

            Date date = Function.getServerDate(con);
            String noKeuangan = KeuanganDAO.getId(con, date);

            String noPembelian = PembelianHeadDAO.getId(con, date);
            p.setNoPembelian(noPembelian);
            p.setTglPembelian(tglSql.format(date));
            PembelianHeadDAO.insert(con, p);
            
            int noUrut = 1;
            for (BebanPembelian beban : p.getListBebanPembelian()) {
                beban.setNoPembelian(noPembelian);
                beban.setNoUrut(noUrut);
                BebanPembelianDAO.insert(con, beban);
                
                noUrut++;
            }
            
            Hutang hutang = new Hutang();
            hutang.setNoHutang(HutangDAO.getId(con, date));
            hutang.setTglHutang(tglSql.format(date));
            hutang.setKategori("Hutang Pembelian");
            hutang.setKeterangan(p.getNoPembelian());
            hutang.setTipeKeuangan("Pembelian");
            hutang.setJumlahHutang(p.getGrandtotal());
            hutang.setPembayaran(p.getPembayaran());
            hutang.setSisaHutang(p.getSisaPembayaran());
            hutang.setKodeUser(sistem.getUser().getKodeUser());
            hutang.setStatus("open");
            HutangDAO.insert(con, hutang);

            insertKeuangan(con, noKeuangan, tglSql.format(date), "Hutang", "Hutang Pembelian",
                    "Pembelian Barang - " + p.getNoPembelian(), p.getSisaPembayaran(), sistem.getUser().getKodeUser());

            double totalQty = 0;
            for (PembelianDetail detail : p.getListPembelianDetail()) {
                totalQty = totalQty + detail.getQty();
            }
            double bebanPerItem = p.getTotalBebanPembelian() / totalQty;

            double totalNilai = 0;
            int noUrutPembelian = 1;
            List<PembelianDetail> groupByBarang = new ArrayList<>();
            for (PembelianDetail detail : p.getListPembelianDetail()) {
                detail.setNoPembelian(noPembelian);
                detail.setNoUrut(noUrutPembelian);
                detail.setNilai(detail.getHargaBeli() + bebanPerItem);
                PembelianDetailDAO.insert(con, detail);

                boolean x = true;
                for (PembelianDetail d : groupByBarang) {
                    if (detail.getKodeBarang().equals(d.getKodeBarang())) {
                        d.setQty(d.getQty() + detail.getQty());
                        d.setNilai(((d.getNilai() * d.getQty()) + (detail.getNilai() * detail.getQty()))
                                / (d.getQty() + detail.getQty()));
                        x = false;
                    }
                }
                if (x) {
                    groupByBarang.add(detail);
                }

                totalNilai = totalNilai + (detail.getNilai() * detail.getQty());
                noUrutPembelian++;
            }

            insertKeuangan(con, noKeuangan, tglSql.format(date), "Stok Barang", "Stok Barang",
                    "Pembelian Barang - " + p.getNoPembelian(), totalNilai, sistem.getUser().getKodeUser());

            for (PembelianDetail d : groupByBarang) {
                double nilai = d.getNilai() * d.getQty();
                
                status = insertStokAndLogBarang(con, date, d.getKodeBarang(), "Pembelian Barang", p.getNoPembelian(), 
                        d.getQty(), nilai, 0, 0, status);
            }

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);

            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                e.printStackTrace();
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String batalPembelian(Connection con, PembelianHead pembelian) {
        try {
            con.setAutoCommit(false);
            String status = "true";

            if (pembelian.getPembayaran() > 0) {
                status = "Tidak dapat dibatalkan, karena sudah ada pembayaran";
            } else {
                Date date = Function.getServerDate(con);
                
                pembelian.setTglBatal(tglSql.format(date));
                pembelian.setUserBatal(sistem.getUser().getKodeUser());
                pembelian.setStatus("false");
                PembelianHeadDAO.update(con, pembelian);

                Hutang hutang = HutangDAO.getByKategoriAndKeteranganAndStatus(
                        con, "Hutang Pembelian", pembelian.getNoPembelian(), "%");
                HutangDAO.delete(con, hutang);

                KeuanganDAO.delete(con, "Hutang", "Hutang Pembelian", "Pembelian Barang - " + pembelian.getNoPembelian());

                pembelian.setListPembelianDetail(PembelianDetailDAO.getAllByNoPembelian(con, pembelian.getNoPembelian()));
                List<PembelianDetail> stokBarang = new ArrayList<>();
                for (PembelianDetail d : pembelian.getListPembelianDetail()) {
                    Boolean inStok = false;
                    for (PembelianDetail detail : stokBarang) {
                        if (d.getKodeBarang().equals(detail.getKodeBarang())) {
                            detail.setQty(detail.getQty() + d.getQty());
                            inStok = true;
                        }
                    }
                    if (!inStok) {
                        stokBarang.add(d);
                    }
                }

                for (PembelianDetail d : stokBarang) {
                    StokBarang stokAkhir = StokBarangDAO.get(con, tglBarang.format(date), d.getKodeBarang());
                    if (stokAkhir == null) {
                        status = "Stok barang " + d.getNamaBarang() + " tidak ditemukan";
                    } else {
                        if (stokAkhir.getStokAkhir() < d.getQty()) {
                            status = "Stok barang " + d.getNamaBarang() + " tidak mencukupi";
                        } else {
                            StokBarang stok = StokBarangDAO.get(con, tglBarang.format(tglSql.parse(pembelian.getTglPembelian())), d.getKodeBarang());
                            stok.setStokMasuk(stok.getStokMasuk() - d.getQty());
                            stok.setStokAkhir(stok.getStokAkhir() - d.getQty());
                            StokBarangDAO.update(con, stok);

                            LogBarangDAO.delete(con, d.getKodeBarang(), "Pembelian Barang", pembelian.getNoPembelian());

                            resetStokDanLogBarang(con, d.getKodeBarang(), pembelian.getTglPembelian(), date);
                        }
                    }
                }
                KeuanganDAO.delete(con, "Stok Barang", "Stok Barang", "Pembelian Barang - " + pembelian.getNoPembelian());
            }
            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);

            return status;
        } catch (Exception e) {
            try {
                e.printStackTrace();
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String newBebanPenjualan(Connection con, BebanPenjualanHead b) {
        try {
            con.setAutoCommit(false);
            String status = "true";

            Date date = Function.getServerDate(con);
            String noKeuangan = KeuanganDAO.getId(con, date);

            b.setNoBebanPenjualan(BebanPenjualanHeadDAO.getId(con, date));
            b.setTglBebanPenjualan(tglSql.format(date));
            BebanPenjualanHeadDAO.insert(con, b);
            
            double totalPenjualan = 0;
            for (BebanPenjualanDetail d : b.getListBebanPenjualanDetail()) {
                totalPenjualan = totalPenjualan + d.getPenjualanHead().getTotalPenjualan();
            }
            for (BebanPenjualanDetail d : b.getListBebanPenjualanDetail()) {
                double beban = d.getPenjualanHead().getTotalPenjualan() / totalPenjualan * b.getTotalBebanPenjualan();
                d.getPenjualanHead().setTotalBebanPenjualan(d.getPenjualanHead().getTotalBebanPenjualan() + beban);
                PenjualanHeadDAO.update(con, d.getPenjualanHead());

                d.setNoBebanPenjualan(b.getNoBebanPenjualan());
                d.setJumlahRp(beban);
                d.setStatus("true");
                BebanPenjualanDetailDAO.insert(con, d);
            }
            insertKeuangan(con, noKeuangan, tglSql.format(date), "Pendapatan/Beban", "Beban Penjualan Langsung",
                    b.getNoBebanPenjualan(), -b.getTotalBebanPenjualan(), sistem.getUser().getKodeUser());
            insertKeuangan(con, noKeuangan, tglSql.format(date), b.getTipeKeuangan(), "Beban Penjualan Langsung",
                    b.getNoBebanPenjualan(), -b.getTotalBebanPenjualan(), sistem.getUser().getKodeUser());

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);
            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String batalBebanPenjualan(Connection con, BebanPenjualanHead b) {
        try {
            con.setAutoCommit(false);
            String status = "true";

            BebanPenjualanHeadDAO.update(con, b);
            for (BebanPenjualanDetail d : b.getListBebanPenjualanDetail()) {
                PenjualanHead p = PenjualanHeadDAO.get(con, d.getNoPenjualan());
                p.setTotalBebanPenjualan(p.getTotalBebanPenjualan() - d.getJumlahRp());
                PenjualanHeadDAO.update(con, p);

                d.setStatus("false");
                BebanPenjualanDetailDAO.update(con, d);
            }
            KeuanganDAO.delete(con, "Pendapatan/Beban", "Beban Penjualan Langsung", b.getNoBebanPenjualan());
            KeuanganDAO.delete(con, b.getTipeKeuangan(), "Beban Penjualan Langsung", b.getNoBebanPenjualan());

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);
            return status;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String newKeuangan(Connection con, Keuangan keu) {
        try {
            con.setAutoCommit(false);
            String status = "true";

            Date date = Function.getServerDate(con);
            String noKeuangan = KeuanganDAO.getId(con, date);

            keu.setNoKeuangan(noKeuangan);
            keu.setTglKeuangan(tglSql.format(date));
            KeuanganDAO.insert(con, keu);

            insertKeuangan(con, noKeuangan, tglSql.format(date), "Pendapatan/Beban", keu.getKategori(),
                    keu.getDeskripsi(), keu.getJumlahRp(), sistem.getUser().getKodeUser());

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);
            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String batalTransaksi(Connection con, Keuangan k) throws Exception {
        try {
            String status = "true";
            con.setAutoCommit(false);

            Date date = Function.getServerDate(con);
            String noKeuangan = KeuanganDAO.getId(con, date);

            insertKeuangan(con, noKeuangan, tglSql.format(date), k.getTipeKeuangan(), k.getKategori(),
                    "Batal Transaksi - " + k.getDeskripsi(), k.getJumlahRp() * -1, sistem.getUser().getKodeUser());
            insertKeuangan(con, noKeuangan, tglSql.format(date), "Pendapatan/Beban", k.getKategori(),
                    "Batal Transaksi - " + k.getDeskripsi(), k.getJumlahRp() * -1, sistem.getUser().getKodeUser());

//            KeuanganDAO.delete(con, "Pendapatan/Beban", k.getKategori(), k.getDeskripsi());
//            KeuanganDAO.delete(con, k.getTipeKeuangan(), k.getKategori(), k.getDeskripsi());
//            
            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);
            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String transferKeuangan(Connection con, String dari, String ke, String keterangan, double jumlahRp) throws Exception {
        try {
            String status = "true";
            con.setAutoCommit(false);

            Date date = Function.getServerDate(con);
            String noKeuangan = KeuanganDAO.getId(con, date);
            insertKeuangan(con, noKeuangan, tglSql.format(date), dari, "Transfer Keuangan",
                    keterangan, jumlahRp * -1, sistem.getUser().getKodeUser());
            insertKeuangan(con, noKeuangan, tglSql.format(date), ke, "Transfer Keuangan",
                    keterangan, jumlahRp, sistem.getUser().getKodeUser());

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);
            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String newHutang(Connection con, Hutang hutang) {
        try {
            con.setAutoCommit(false);
            String status = "true";

            Date date = Function.getServerDate(con);
            String noKeuangan = KeuanganDAO.getId(con, date);

            hutang.setNoHutang(HutangDAO.getId(con, date));
            hutang.setTglHutang(tglSql.format(date));
            HutangDAO.insert(con, hutang);

            Keuangan k = new Keuangan();
            k.setNoKeuangan(noKeuangan);
            k.setTglKeuangan(tglSql.format(date));
            k.setTipeKeuangan(hutang.getTipeKeuangan());
            k.setKategori(hutang.getKategori());
            k.setDeskripsi(hutang.getNoHutang() + " - " + hutang.getKeterangan());
            k.setJumlahRp(hutang.getJumlahHutang());
            k.setKodeUser(sistem.getUser().getKodeUser());
            KeuanganDAO.insert(con, k);

            Keuangan h = new Keuangan();
            h.setNoKeuangan(noKeuangan);
            h.setTglKeuangan(tglSql.format(date));
            h.setTipeKeuangan("Hutang");
            h.setKategori(hutang.getKategori());
            h.setDeskripsi(hutang.getNoHutang() + " - " + hutang.getKeterangan());
            h.setJumlahRp(hutang.getJumlahHutang());
            h.setKodeUser(sistem.getUser().getKodeUser());
            KeuanganDAO.insert(con, h);

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);

            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String newPembayaranHutang(Connection con, Pembayaran pembayaran) {
        try {
            con.setAutoCommit(false);
            String status = "true";

            Date date = Function.getServerDate(con);
            String noKeuangan = KeuanganDAO.getId(con, date);

            pembayaran.setNoPembayaran(PembayaranDAO.getId(con, date));
            pembayaran.setTglPembayaran(tglSql.format(date));
            PembayaranDAO.insert(con, pembayaran);

            Hutang hutang = pembayaran.getHutang();
            hutang.setPembayaran(hutang.getPembayaran() + pembayaran.getJumlahPembayaran());
            hutang.setSisaHutang(hutang.getSisaHutang() - pembayaran.getJumlahPembayaran());
            if (hutang.getSisaHutang() == 0) {
                hutang.setStatus("close");
            }
            HutangDAO.update(con, hutang);

            if (pembayaran.getHutang().getKategori().equals("Hutang Pembelian")) {
                PembelianHead pembelian = PembelianHeadDAO.get(con, hutang.getKeterangan());
                pembelian.setPembayaran(pembelian.getPembayaran() + pembayaran.getJumlahPembayaran());
                pembelian.setSisaPembayaran(pembelian.getSisaPembayaran() - pembayaran.getJumlahPembayaran());
                PembelianHeadDAO.update(con, pembelian);
            }

            Keuangan keuPembayaran = new Keuangan();
            keuPembayaran.setNoKeuangan(noKeuangan);
            keuPembayaran.setTglKeuangan(tglSql.format(date));
            keuPembayaran.setTipeKeuangan(pembayaran.getTipeKeuangan());
            keuPembayaran.setKategori(hutang.getKategori());
            keuPembayaran.setDeskripsi("Pembayaran - " + hutang.getKeterangan() + " (" + pembayaran.getNoPembayaran() + ")");
            keuPembayaran.setJumlahRp(-pembayaran.getJumlahPembayaran());
            keuPembayaran.setKodeUser(sistem.getUser().getKodeUser());
            KeuanganDAO.insert(con, keuPembayaran);

            Keuangan pb = new Keuangan();
            pb.setNoKeuangan(noKeuangan);
            pb.setTglKeuangan(tglSql.format(date));
            pb.setTipeKeuangan("Hutang");
            pb.setKategori(hutang.getKategori());
            pb.setDeskripsi("Pembayaran - " + hutang.getKeterangan() + " (" + pembayaran.getNoPembayaran() + ")");
            pb.setJumlahRp(-pembayaran.getJumlahPembayaran());
            pb.setKodeUser(sistem.getUser().getKodeUser());
            KeuanganDAO.insert(con, pb);

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);

            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String batalPembayaranHutang(Connection con, Pembayaran pembayaran) {
        try {
            con.setAutoCommit(false);
            String status = "true";

            Date date = Function.getServerDate(con);

            pembayaran.setTglBatal(tglSql.format(date));
            pembayaran.setUserBatal(sistem.getUser().getKodeUser());
            pembayaran.setStatus("false");
            PembayaranDAO.update(con, pembayaran);

            Hutang hutang = HutangDAO.get(con, pembayaran.getNoHutang());
            hutang.setPembayaran(hutang.getPembayaran() - pembayaran.getJumlahPembayaran());
            hutang.setSisaHutang(hutang.getSisaHutang() + pembayaran.getJumlahPembayaran());
            if (hutang.getSisaHutang() != 0) {
                hutang.setStatus("open");
            }
            HutangDAO.update(con, hutang);

            if (hutang.getKategori().equals("Hutang Pembelian")) {
                PembelianHead pembelian = PembelianHeadDAO.get(con, hutang.getKeterangan());
                pembelian.setPembayaran(pembelian.getPembayaran() - pembayaran.getJumlahPembayaran());
                pembelian.setSisaPembayaran(pembelian.getSisaPembayaran() + pembayaran.getJumlahPembayaran());
                PembelianHeadDAO.update(con, pembelian);
            }

            KeuanganDAO.delete(con, pembayaran.getTipeKeuangan(), hutang.getKategori(),
                    "Pembayaran - " + hutang.getKeterangan() + " (" + pembayaran.getNoPembayaran() + ")");

            KeuanganDAO.delete(con, "Hutang", hutang.getKategori(),
                    "Pembayaran - " + hutang.getKeterangan() + " (" + pembayaran.getNoPembayaran() + ")");

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);

            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String newPiutang(Connection con, Piutang piutang) {
        try {
            con.setAutoCommit(false);
            String status = "true";

            Date date = Function.getServerDate(con);
            String noKeuangan = KeuanganDAO.getId(con, date);

            piutang.setNoPiutang(PiutangDAO.getId(con, date));
            piutang.setTglPiutang(tglSql.format(date));
            PiutangDAO.insert(con, piutang);
            
            Keuangan keuangan = new Keuangan();
            keuangan.setNoKeuangan(noKeuangan);
            keuangan.setTglKeuangan(tglSql.format(date));
            keuangan.setTipeKeuangan(piutang.getTipeKeuangan());
            keuangan.setKategori(piutang.getKategori());
            keuangan.setDeskripsi(piutang.getNoPiutang() + " - " + piutang.getKeterangan());
            keuangan.setJumlahRp(piutang.getJumlahPiutang() * -1);
            keuangan.setKodeUser(sistem.getUser().getKodeUser());
            KeuanganDAO.insert(con, keuangan);

            Keuangan p = new Keuangan();
            p.setNoKeuangan(noKeuangan);
            p.setTglKeuangan(tglSql.format(date));
            p.setTipeKeuangan("Piutang");
            p.setKategori(piutang.getKategori());
            p.setDeskripsi(piutang.getNoPiutang() + " - " + piutang.getKeterangan());
            p.setJumlahRp(piutang.getJumlahPiutang());
            p.setKodeUser(sistem.getUser().getKodeUser());
            KeuanganDAO.insert(con, p);


            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);

            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String newTerimaPembayaranPiutang(Connection con, TerimaPembayaran terimaPembayaran) {
        try {
            con.setAutoCommit(false);
            String status = "true";

            Date date = Function.getServerDate(con);
            String noKeuangan = KeuanganDAO.getId(con, date);

            terimaPembayaran.setNoTerimaPembayaran(TerimaPembayaranDAO.getId(con, date));
            terimaPembayaran.setTglTerima(tglSql.format(date));
            TerimaPembayaranDAO.insert(con, terimaPembayaran);

            Piutang piutang = terimaPembayaran.getPiutang();
            piutang.setPembayaran(piutang.getPembayaran() + terimaPembayaran.getJumlahPembayaran());
            piutang.setSisaPiutang(piutang.getSisaPiutang() - terimaPembayaran.getJumlahPembayaran());
            if (piutang.getSisaPiutang() == 0) {
                piutang.setStatus("close");
            }
            PiutangDAO.update(con, piutang);

            if (piutang.getKategori().equals("Piutang Penjualan")) {
                PenjualanHead penjualan = PenjualanHeadDAO.get(con, piutang.getKeterangan());
                penjualan.setPembayaran(penjualan.getPembayaran() + terimaPembayaran.getJumlahPembayaran());
                penjualan.setSisaPembayaran(penjualan.getSisaPembayaran() - terimaPembayaran.getJumlahPembayaran());
                PenjualanHeadDAO.update(con, penjualan);
            }

            Keuangan keuTerimaPembayaran = new Keuangan();
            keuTerimaPembayaran.setNoKeuangan(noKeuangan);
            keuTerimaPembayaran.setTglKeuangan(tglSql.format(date));
            keuTerimaPembayaran.setTipeKeuangan(terimaPembayaran.getTipeKeuangan());
            keuTerimaPembayaran.setKategori(piutang.getKategori());
            keuTerimaPembayaran.setDeskripsi("Terima Pembayaran - " + piutang.getKeterangan() + " (" + terimaPembayaran.getNoTerimaPembayaran() + ")");
            keuTerimaPembayaran.setJumlahRp(terimaPembayaran.getJumlahPembayaran());
            keuTerimaPembayaran.setKodeUser(sistem.getUser().getKodeUser());
            KeuanganDAO.insert(con, keuTerimaPembayaran);

            Keuangan keuPiutang = new Keuangan();
            keuPiutang.setNoKeuangan(noKeuangan);
            keuPiutang.setTglKeuangan(tglSql.format(date));
            keuPiutang.setTipeKeuangan("Piutang");
            keuPiutang.setKategori(piutang.getKategori());
            keuPiutang.setDeskripsi("Terima Pembayaran - " + piutang.getKeterangan() + " (" + terimaPembayaran.getNoTerimaPembayaran() + ")");
            keuPiutang.setJumlahRp(-terimaPembayaran.getJumlahPembayaran());
            keuPiutang.setKodeUser(sistem.getUser().getKodeUser());
            KeuanganDAO.insert(con, keuPiutang);

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);

            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String batalTerimaPembayaranPiutang(Connection con, TerimaPembayaran terimaPembayaran) {
        try {
            con.setAutoCommit(false);
            String status = "true";

            Date date = Function.getServerDate(con);
            
            terimaPembayaran.setTglBatal(tglSql.format(date));
            terimaPembayaran.setUserBatal(sistem.getUser().getKodeUser());
            terimaPembayaran.setStatus("false");
            TerimaPembayaranDAO.update(con, terimaPembayaran);

            Piutang piutang = PiutangDAO.get(con, terimaPembayaran.getNoPiutang());
            piutang.setPembayaran(piutang.getPembayaran() - terimaPembayaran.getJumlahPembayaran());
            piutang.setSisaPiutang(piutang.getSisaPiutang() + terimaPembayaran.getJumlahPembayaran());
            if (piutang.getSisaPiutang() != 0) {
                piutang.setStatus("open");
            }
            PiutangDAO.update(con, piutang);

            if (piutang.getKategori().equals("Piutang Penjualan")) {
                PenjualanHead penjualan = PenjualanHeadDAO.get(con, piutang.getKeterangan());
                penjualan.setPembayaran(penjualan.getPembayaran() - terimaPembayaran.getJumlahPembayaran());
                penjualan.setSisaPembayaran(penjualan.getSisaPembayaran() + terimaPembayaran.getJumlahPembayaran());
                PenjualanHeadDAO.update(con, penjualan);
            }
            KeuanganDAO.delete(con, terimaPembayaran.getTipeKeuangan(), piutang.getKategori(),
                    "Terima Pembayaran - " + piutang.getKeterangan() + " (" + terimaPembayaran.getNoTerimaPembayaran() + ")");

            KeuanganDAO.delete(con, "Piutang", piutang.getKategori(),
                    "Terima Pembayaran - " + piutang.getKeterangan() + " (" + terimaPembayaran.getNoTerimaPembayaran() + ")");

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);

            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String newModal(Connection con, Keuangan k) {
        try {
            con.setAutoCommit(false);
            String status = "true";

            Date date = Function.getServerDate(con);
            String noKeuangan = KeuanganDAO.getId(con, date);

            k.setNoKeuangan(noKeuangan);
            KeuanganDAO.insert(con, k);

            Keuangan modal = new Keuangan();
            modal.setNoKeuangan(noKeuangan);
            modal.setTglKeuangan(tglSql.format(date));
            modal.setTipeKeuangan("Modal");
            modal.setKategori(k.getKategori());
            modal.setDeskripsi(k.getDeskripsi());
            modal.setJumlahRp(k.getJumlahRp());
            modal.setKodeUser(sistem.getUser().getKodeUser());
            KeuanganDAO.insert(con, modal);

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);

            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String pembelianAsetTetap(Connection con, AsetTetap aset, String tipeKeuangan) {
        try {
            con.setAutoCommit(false);
            String status = "true";

            Date date = Function.getServerDate(con);
            String noKeuangan = KeuanganDAO.getId(con, date);
            aset.setNoAset(AsetTetapDAO.getId(con, date));
            aset.setTglBeli(tglSql.format(date));
            AsetTetapDAO.insert(con, aset);

            Keuangan k = new Keuangan();
            k.setNoKeuangan(noKeuangan);
            k.setTglKeuangan(tglSql.format(date));
            k.setTipeKeuangan(tipeKeuangan);
            k.setKategori("Pembelian Aset Tetap");
            k.setDeskripsi(aset.getNoAset());
            k.setJumlahRp(-aset.getNilaiAwal());
            k.setKodeUser(sistem.getUser().getKodeUser());
            KeuanganDAO.insert(con, k);

            Keuangan keuangan = new Keuangan();
            keuangan.setNoKeuangan(noKeuangan);
            keuangan.setTglKeuangan(tglSql.format(date));
            keuangan.setTipeKeuangan("Aset Tetap");
            keuangan.setKategori(aset.getKategori());
            keuangan.setDeskripsi("Pembelian Aset Tetap - " + aset.getNoAset());
            keuangan.setJumlahRp(aset.getNilaiAwal());
            keuangan.setKodeUser(sistem.getUser().getKodeUser());
            KeuanganDAO.insert(con, keuangan);

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);
            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String penjualanAsetTetap(Connection con, AsetTetap aset, String tipeKeuangan) {
        try {
            con.setAutoCommit(false);
            String status = "true";

            Date date = Function.getServerDate(con);
            String noKeuangan = KeuanganDAO.getId(con, date);
            aset.setTglJual(tglSql.format(date));
            AsetTetapDAO.update(con, aset);

            Keuangan k = new Keuangan();
            k.setNoKeuangan(noKeuangan);
            k.setTglKeuangan(tglSql.format(date));
            k.setTipeKeuangan(tipeKeuangan);
            k.setKategori("Penjualan Aset Tetap");
            k.setDeskripsi(aset.getNoAset());
            k.setJumlahRp(aset.getHargaJual());
            k.setKodeUser(sistem.getUser().getKodeUser());
            KeuanganDAO.insert(con, k);

            Keuangan asetTetap = new Keuangan();
            asetTetap.setNoKeuangan(noKeuangan);
            asetTetap.setTglKeuangan(tglSql.format(date));
            asetTetap.setTipeKeuangan("Aset Tetap");
            asetTetap.setKategori(aset.getKategori());
            asetTetap.setDeskripsi("Penjualan Aset Tetap - " + aset.getNoAset());
            asetTetap.setJumlahRp(-aset.getNilaiAkhir());
            asetTetap.setKodeUser(sistem.getUser().getKodeUser());
            KeuanganDAO.insert(con, asetTetap);

            if (aset.getHargaJual() > aset.getNilaiAkhir()) {
                Keuangan pendapatan = new Keuangan();
                pendapatan.setNoKeuangan(noKeuangan);
                pendapatan.setTglKeuangan(tglSql.format(date));
                pendapatan.setTipeKeuangan("Pendapatan");
                pendapatan.setKategori("Pendapatan Penjualan Aset Tetap");
                pendapatan.setDeskripsi("Penjualan Aset Tetap - " + aset.getNoAset());
                pendapatan.setJumlahRp(aset.getHargaJual() - aset.getNilaiAkhir());
                pendapatan.setKodeUser(sistem.getUser().getKodeUser());
                KeuanganDAO.insert(con, pendapatan);
            } else if (aset.getHargaJual() < aset.getNilaiAkhir()) {
                Keuangan beban = new Keuangan();
                beban.setNoKeuangan(noKeuangan);
                beban.setTglKeuangan(tglSql.format(date));
                beban.setTipeKeuangan("Beban");
                beban.setKategori("Beban Penjualan Aset Tetap");
                beban.setDeskripsi("Penjualan Aset Tetap - " + aset.getNoAset());
                beban.setJumlahRp(aset.getNilaiAkhir() - aset.getHargaJual());
                beban.setKodeUser(sistem.getUser().getKodeUser());
                KeuanganDAO.insert(con, beban);
            }

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);

            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static String savePenyesuaianStokBarang(Connection con, PenyesuaianStokBarang p) {
        try {
            con.setAutoCommit(false);
            String status = "true";

            LogBarang logBarang = LogBarangDAO.getLastByBarang(con, p.getKodeBarang());
//            logBarang.setStokAkhir(Math.round(logBarang.getStokAkhir() * 100) / 100);
            if (logBarang.getStokAkhir() + p.getQty() < 0) {
                status = "Stok barang " + p.getKodeBarang() + " tidak mencukupi";
            } else {
                Date date = Function.getServerDate(con);
                String noKeuangan = KeuanganDAO.getId(con, date);

                p.setNoPenyesuaian(PenyesuaianStokBarangDAO.getId(con, date));
                p.setTglPenyesuaian(tglSql.format(date));
                
                double qty = p.getQty();
                double nilai = 0;
                if (logBarang.getStokAkhir() != 0) {
                    nilai = logBarang.getNilaiAkhir() / logBarang.getStokAkhir() * p.getQty();
                }

                if (qty < 0) {
                    status = insertStokAndLogBarang(con, date, p.getKodeBarang(), 
                            "Penyesuaian Stok Barang", p.getNoPenyesuaian(), 0, 0, qty * -1, nilai * -1, status);
                } else {
                    status = insertStokAndLogBarang(con, date, p.getKodeBarang(), 
                            "Penyesuaian Stok Barang", p.getNoPenyesuaian(), qty, nilai, 0, 0, status);
                }

                Keuangan k = new Keuangan();
                k.setNoKeuangan(noKeuangan);
                k.setTglKeuangan(tglSql.format(date));
                k.setTipeKeuangan("Stok Barang");
                k.setKategori("Stok Barang");
                k.setDeskripsi("Penyesuaian Stok Barang - " + p.getNoPenyesuaian());
                k.setJumlahRp(nilai);
                k.setKodeUser(sistem.getUser().getKodeUser());
                KeuanganDAO.insert(con, k);

                Keuangan k2 = new Keuangan();
                k2.setNoKeuangan(noKeuangan);
                k2.setTglKeuangan(tglSql.format(date));
                k2.setTipeKeuangan("Pendapatan/Beban");
                k2.setKategori("Penyesuaian Stok Barang");
                k2.setDeskripsi("Penyesuaian Stok Barang - " + p.getNoPenyesuaian());
                k2.setJumlahRp(nilai);
                k2.setKodeUser(sistem.getUser().getKodeUser());
                KeuanganDAO.insert(con, k2);

                p.setNilai(nilai);
                PenyesuaianStokBarangDAO.insert(con, p);
            }

            if (status.equals("true")) {
                con.commit();
            } else {
                con.rollback();
            }
            con.setAutoCommit(true);
            return status;
        } catch (Exception e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
                return e.toString();
            } catch (SQLException ex) {
                return ex.toString();
            }
        }
    }

    public static void resetStokDanLogBarang(Connection con, String kodeBarang, String tglTransaksi, Date now) throws Exception {
        List<StokBarang> listStokBarang = StokBarangDAO.getAllByTanggalAndBarang(
                con, tglBarang.format(tglSql.parse(tglTransaksi)), tglBarang.format(now),
                kodeBarang);
        listStokBarang.sort(Comparator.comparing(StokBarang::getTanggal));
        double stokBarang = listStokBarang.get(0).getStokAwal();
        for (StokBarang s : listStokBarang) {
            s.setStokAwal(stokBarang);
            stokBarang = stokBarang + s.getStokMasuk() - s.getStokKeluar();
            s.setStokAkhir(stokBarang);
            StokBarangDAO.update(con, s);
        }
        List<LogBarang> listBarang = LogBarangDAO.getAllByTanggalAndBarang(
                con, tglBarang.format(tglSql.parse(tglTransaksi)), tglBarang.format(now), kodeBarang);
        LogBarang logBarang = LogBarangDAO.getLastBeforeDateAndBarang(
                con, tglBarang.format(tglSql.parse(tglTransaksi)), kodeBarang);
        listBarang.sort(Comparator.comparing(LogBarang::getTanggal));
        double stok = logBarang.getStokAkhir();
        double nilai = logBarang.getNilaiAkhir();
        for (LogBarang log : listBarang) {
            log.setStokAwal(stok);
            log.setNilaiAwal(nilai);

            stok = stok + log.getStokMasuk() - log.getStokKeluar();
            nilai = nilai + log.getNilaiMasuk() - log.getNilaiKeluar();

            log.setStokAkhir(stok);
            log.setNilaiAkhir(nilai);

            LogBarangDAO.update(con, log);
        }
    }

    public static String insertStokAndLogBarang(Connection con, Date date, String kodeBarang, String kategori, String keterangan,
            double qtyIn, double nilaiIn, double qtyOut, double nilaiOut, String status) throws Exception {
        if (qtyIn != 0 || qtyOut != 0) {
            StokBarang stokBarang = StokBarangDAO.get(con, tglBarang.format(date), kodeBarang);
            if (stokBarang == null) {
                status = "Stok barang " + kodeBarang + " tidak ditemukan";
            } 
            else if (qtyOut>0 && stokBarang.getStokAkhir() < qtyOut) {
                status = "Stok barang " + kodeBarang + " tidak mencukupi";
            } 
            else {
                if (stokBarang.getTanggal().equals(tglBarang.format(date))) {
                    stokBarang.setStokMasuk(stokBarang.getStokMasuk() + qtyIn);
                    stokBarang.setStokKeluar(stokBarang.getStokKeluar() + qtyOut);
                    stokBarang.setStokAkhir(stokBarang.getStokAkhir() + qtyIn - qtyOut);
                    StokBarangDAO.update(con, stokBarang);
                } else {
                    StokBarang stok = new StokBarang();
                    stok.setTanggal(tglBarang.format(date));
                    stok.setKodeBarang(stokBarang.getKodeBarang());
                    stok.setStokAwal(stokBarang.getStokAkhir());
                    stok.setStokMasuk(qtyIn);
                    stok.setStokKeluar(qtyOut);
                    stok.setStokAkhir(stokBarang.getStokAkhir() + qtyIn - qtyOut);
                    StokBarangDAO.insert(con, stok);
                }
                LogBarang logUmum = LogBarangDAO.getLastByBarang(con, kodeBarang);
                LogBarang logBarang = new LogBarang();
                logBarang.setTanggal(tglSql.format(date));
                logBarang.setKodeBarang(logUmum.getKodeBarang());
                logBarang.setKategori(kategori);
                logBarang.setKeterangan(keterangan);
                logBarang.setStokAwal(logUmum.getStokAkhir());
                logBarang.setNilaiAwal(logUmum.getNilaiAkhir());
                logBarang.setStokMasuk(qtyIn);
                logBarang.setNilaiMasuk(nilaiIn);
                logBarang.setStokKeluar(qtyOut);
                logBarang.setNilaiKeluar(nilaiOut);
                logBarang.setStokAkhir(logUmum.getStokAkhir() + qtyIn - qtyOut);
                logBarang.setNilaiAkhir(logUmum.getNilaiAkhir() + nilaiIn - nilaiOut);
                LogBarangDAO.insert(con, logBarang);
            }
        }
        return status;
    }

//    public static String newReturPenjualan(Connection con, ReturPenjualanHead retur){
//        try{
//            con.setAutoCommit(false);
//            String status = "true";
//            
//            ReturPenjualanHeadDAO.insert(con, retur);
//            
//            String noKeuangan = KeuanganDAO.getId(con);
//            insertKeuangan(con, noKeuangan, tglSql.format(Function.getServerDate(con)), retur.getTipeKeuangan(), "Retur Penjualan", 
//                    "Retur Penjualan - "+retur.getNoReturPenjualan(), -retur.getTotalRetur(), sistem.getUser().getKodeUser());
//            insertKeuangan(con, noKeuangan, tglSql.format(Function.getServerDate(con)), "Retur Penjualan", "Retur Penjualan", 
//                    "Retur Penjualan - "+retur.getNoReturPenjualan(), retur.getTotalRetur(), sistem.getUser().getKodeUser());
//            
//            double hpp = 0;
//            List<ReturPenjualanDetail> stokBarang = new ArrayList<>();
//            for(ReturPenjualanDetail d : retur.getListReturPenjualanDetail()){
//                ReturPenjualanDetailDAO.insert(con, d);
//                
//                hpp = hpp + d.getNilai()*d.getQty();
//                
//                Boolean inStok = false;
//                for(ReturPenjualanDetail detail : stokBarang){
//                    if(d.getKodeBarang().equals(detail.getKodeBarang())){
//                        detail.setNilai((detail.getNilai()*detail.getQty()+d.getNilai()*d.getQty())/
//                                (detail.getQty()+d.getQty()));
//                        detail.setQty(detail.getQty()+d.getQty());
//                        inStok = true;
//                    }
//                }
//                if(!inStok)
//                    stokBarang.add(d);
//            }
//            insertKeuangan(con, noKeuangan, tglSql.format(Function.getServerDate(con)), "HPP", "Retur Penjualan", 
//                    "Retur Penjualan - "+retur.getNoReturPenjualan(), -hpp, sistem.getUser().getKodeUser());
//            
//            for(ReturPenjualanDetail d : stokBarang){
//                StokBarang stok = StokBarangDAO.get(con, tglBarang.format(Function.getServerDate(con)), d.getKodeBarang());
//                if(stok==null){
//                    StokBarang newStok = new StokBarang();
//                    newStok.setTanggal(tglBarang.format(Function.getServerDate(con)));
//                    newStok.setKodeBarang(d.getKodeBarang());
//                    newStok.setStokAwal(0);
//                    newStok.setStokMasuk(d.getQty());
//                    newStok.setStokKeluar(0);
//                    newStok.setStokAkhir(d.getQty());
//                    StokBarangDAO.insert(con, newStok);
//                }else{
//                    if(stok.getTanggal().equals(tglBarang.format(Function.getServerDate(con)))){
//                        stok.setStokMasuk(stok.getStokMasuk()+d.getQty());
//                        stok.setStokAkhir(stok.getStokAkhir()+d.getQty());
//                        StokBarangDAO.update(con, stok);
//                    }else{
//                        StokBarang newStok = new StokBarang();
//                        newStok.setTanggal(tglBarang.format(Function.getServerDate(con)));
//                        newStok.setKodeBarang(d.getKodeBarang());
//                        newStok.setStokAwal(stok.getStokAkhir());
//                        newStok.setStokMasuk(d.getQty());
//                        newStok.setStokKeluar(0);
//                        newStok.setStokAkhir(stok.getStokAkhir()+d.getQty());
//                        StokBarangDAO.insert(con, newStok);
//                    }
//                }
//                LogBarang lb = LogBarangDAO.getLastByBarang(con, d.getKodeBarang());
//                double nilai = d.getNilai()* d.getQty();
//
//                LogBarang log = new LogBarang();
//                log.setTanggal(tglSql.format(Function.getServerDate(con)));
//                log.setKodeBarang(d.getKodeBarang());
//                log.setKategori("Retur Penjualan");
//                log.setKeterangan(retur.getNoReturPenjualan());
//                log.setStokAwal(lb.getStokAkhir());
//                log.setNilaiAwal(lb.getNilaiAkhir());
//                log.setStokMasuk(d.getQty());
//                log.setNilaiMasuk(nilai);
//                log.setStokKeluar(0);
//                log.setNilaiKeluar(0);
//                log.setStokAkhir(lb.getStokAkhir()+d.getQty());
//                log.setNilaiAkhir(lb.getNilaiAkhir()+nilai);
//                LogBarangDAO.insert(con, log);
//
//                insertKeuangan(con, noKeuangan, tglSql.format(Function.getServerDate(con)), "Stok Barang", d.getKodeBarang(), 
//                        "Retur Penjualan - "+retur.getNoReturPenjualan(), nilai, sistem.getUser().getKodeUser());
//            }
//            if(status.equals("true"))
//                con.commit();
//            else 
//                con.rollback();
//            con.setAutoCommit(true);
//            
//            return status;
//        }catch(Exception e){
//            try{
//                con.rollback();
//            con.setAutoCommit(true);
//                return e.toString();
//            }catch(SQLException ex){
//                return ex.toString();
//            }
//        }
//    }
//    public static String batalReturPenjualan(Connection con, ReturPenjualanHead retur){
//        try{
//            con.setAutoCommit(false);
//            String status = "true";
//            
//            ReturPenjualanHeadDAO.update(con, retur);
//            
//            KeuanganDAO.delete(con, retur.getTipeKeuangan(), "Retur Penjualan", "Retur Penjualan - "+retur.getNoReturPenjualan());
//            KeuanganDAO.delete(con, "Retur Penjualan", "Retur Penjualan", "Retur Penjualan - "+retur.getNoReturPenjualan());
//            
//            retur.setListReturPenjualanDetail(ReturPenjualanDetailDAO.getAllByNoRetur(con, retur.getNoReturPenjualan()));
//            double hpp = 0;
//            List<ReturPenjualanDetail> stokBarang = new ArrayList<>();
//            for(ReturPenjualanDetail d : retur.getListReturPenjualanDetail()){
//                hpp = hpp + d.getNilai()*d.getQty();
//                
//                Boolean inStok = false;
//                for(ReturPenjualanDetail detail : stokBarang){
//                    if(d.getKodeBarang().equals(detail.getKodeBarang())){
//                        detail.setNilai((detail.getNilai()*detail.getQty()+d.getNilai()*d.getQty())/
//                                (detail.getQty()+d.getQty()));
//                        detail.setQty(detail.getQty()+d.getQty());
//                        inStok = true;
//                    }
//                }
//                if(!inStok)
//                    stokBarang.add(d);
//            }
//            KeuanganDAO.delete(con, "HPP", "HPP", "Retur Penjualan - "+retur.getNoReturPenjualan());
//            
//            for(ReturPenjualanDetail d : stokBarang){
//                StokBarang stok = StokBarangDAO.get(con, tglBarang.format(tglSql.parse(retur.getTglReturPenjualan())), d.getKodeBarang());
//                if(stok==null)
//                    status = "Stok barang "+d.getKodeBarang()+" tidak ditemukan";
//                else if(stok.getStokAkhir()<d.getQty())
//                    status = "Stok barang "+d.getKodeBarang()+" tidak mencukupi";
//                else{    
//                    stok.setStokMasuk(stok.getStokMasuk()-d.getQty());
//                    stok.setStokAkhir(stok.getStokAkhir()-d.getQty());
//                    StokBarangDAO.update(con, stok);
//                    resetStokBarang(con, d.getKodeBarang(), tglBarang.format(tglSql.parse(retur.getTglReturPenjualan())));
//
//                    LogBarangDAO.delete(con, d.getKodeBarang(), "Retur Penjualan", retur.getNoReturPenjualan());
//                    resetLogBarang(con, retur.getTglReturPenjualan(), d.getKodeBarang());
//
//                    KeuanganDAO.delete(con, "Stok Barang", d.getKodeBarang(), "Retur Penjualan - "+retur.getNoReturPenjualan());
//                }
//            }
//
//            if(status.equals("true"))
//                con.commit();
//            else 
//                con.rollback();
//            con.setAutoCommit(true);
//            
//            return status;
//        }catch(Exception e){
//            try{
//                con.rollback();
//                con.setAutoCommit(true);
//                return e.toString();
//            }catch(SQLException ex){
//                return ex.toString();
//            }
//        }
//    }
//    public static String saveAbsensiManual(Connection con ,Absensi a){
//        try{
//            con.setAutoCommit(false);
//            String status = "true";
//            
//            Absensi alama = AbsensiDAO.get(con, a.getTanggal(), a.getKodePegawai());
//            if(alama==null){
//                AbsensiDAO.insert(con, a);
//            }else{
//                AbsensiDAO.update(con, a);
//            }
//            
//            if(status.equals("true"))
//                con.commit();
//            else
//                con.rollback();
//            con.setAutoCommit(true);
//            return status;
//        }catch(Exception e){
//            try{
//                con.rollback();
//                con.setAutoCommit(true);
//                return e.toString();
//            }catch(SQLException ex){
//                return ex.toString();
//            }
//        }
//    }
}
