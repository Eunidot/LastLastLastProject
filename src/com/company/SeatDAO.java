package com.company;

import java.sql.ResultSet;

public class SeatDAO {
    DAOManager daoManager = AppManager.getInstance().getDAOManager();
    ResultSet rs;

    public SeatDAO() {
        AppManager.getInstance().getDAOManager().setSeatDAO(this);
    }

    public void setSelectedSeat (String seatname) {
        daoManager.connectDB();

        String sql = "UPDATE seat SET booking=1 WHERE seat_UID=?";
        try{
            daoManager.pstmt = daoManager.conn.prepareStatement(sql);
            daoManager.pstmt.setString(1, seatname);
            daoManager.pstmt.executeUpdate();
            daoManager.closeDB();
        }catch (Exception e){
            System.out.println("newSeatDAO Error");
        }
    }
    public int getBooking (String seatname) {
        daoManager.connectDB();

        String sql = "SELECT booking FROM seat WHERE seat_UID=?";
        int book=0;
        try{
            daoManager.pstmt = daoManager.conn.prepareStatement(sql);
            daoManager.pstmt.setString(1, seatname);
            rs = daoManager.pstmt.executeQuery();
            if(rs.next())
                book =rs.getInt("booking");
            daoManager.closeDB();
        }catch (Exception e){
            System.out.println("newSeatDAO Error");
        }
        return book;
    }
}
