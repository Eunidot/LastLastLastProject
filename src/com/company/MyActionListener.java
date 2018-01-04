package com.company;

import javafx.scene.control.Spinner;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLOutput;

public class MyActionListener {
    LoginView loginView = new LoginView();
    DataManager dataManager = new DataManager();
    Customer customer = new Customer();
    DAOManager daoManager = new DAOManager();
    CustomerDAO customerDAO = new CustomerDAO();
    SnackOrder snackOrder = new SnackOrder();
    SnackOrderDAO snackOrderDAO = new SnackOrderDAO();
    MainView mainView;
    LoginActionL lL = new LoginActionL();
    JoinActionL  jL = new JoinActionL();
    MainViewActionL mL = new MainViewActionL();
    JspinnerChangeL cL = new JspinnerChangeL();
    private int[] sum = new int[12];
    SnackDAO snackDAO = new SnackDAO();
    Ticket ticket = new Ticket();
    TicketDAO ticketDAO = new TicketDAO();
    SeatDAO seatDAO = new SeatDAO();

    int aultTiketnum = 0;
    int studentTiketnum = 0;
    String[] seatselect = new String[4];

    Movie curMovie = new Movie();

    int allPrice=0; // 총 결제 금액
    String pString = "팝콘 : "; // 결제패널 팝콘 문자열
    String bString = "음료 : "; // 결제패널 음료 문자열

    APIMovie apiMovie = new APIMovie();

    public MyActionListener() {
        AppManager.getInstance().setMyactionListener(this);
    }
    //---------------------------------------------------------------------------------------------로그인 화면에서의 액션리스너
    class LoginActionL implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Object obj =e.getSource();
            //로그인 버튼
            if(obj==loginView.btnLogin) {
                customer.setId(loginView.tfLogin[0].getText());
                customer.setPw(loginView.tfLogin[1].getText());
                // DB에 있는 id와 pw가 일치
                if(customerDAO.login()){
                    // 메인 뷰 보여주기
                    mainView = new MainView();
                    MainViewListenerSet();
                    JSpinnerChangeListenerSet();
                }
                else {
                    // 로그인 실패 다이얼로그 띄워주기============================================================================================================================
                	loginView.diaNoti.showMessageDialog(loginView.frame, "로그인이 실패되었습니다.", "안내", loginView.diaNoti.ERROR_MESSAGE);
                    
               
                }
            }
            //회원가입 버튼
            else if(obj==loginView.btnJoin) {
                loginView.joinUI();
            }
        } // actionPerformed()
    } //LoginActionL class

    //---------------------------------------------------------------------------------------------회원가입 화면에서 액션리스너
    class JoinActionL implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Object obj = e.getSource();
            //가입 버튼
            if (obj == loginView.btn[0]) {
                // 빈 공백이 있다면
                if(!blankCheck()){
                    // 입력하지 않은 정보가 있습니다. 문구 띄워주기==================================================================================================================
                	loginView.diaNoti.showMessageDialog(loginView.frame, "입력하지 않은 정보가 있습니다.", "안내", loginView.diaNoti.ERROR_MESSAGE);
                                
                }
                else {
                    // 회원 정보 객체에 담기
                    customer.setId(loginView.tfJoin[0].getText());
                    customer.setPw(loginView.tfJoin[1].getText());
                    customer.setName(loginView.tfJoin[2].getText());
                    customer.setTel(loginView.tfJoin[3].getText());
                    for (int i = 0; i < 6; i++) {
                        if (loginView.rb[i].isSelected()) {
                            customer.setGenre(loginView.rb[i].getText());
                        }
                    }

                    // 회원 가입
                    customerDAO.newCustomer();

                    // 회원 가입 후 화면 끄기
                    loginView.diaJoin.dispose();

                    loginView.diaNoti.showMessageDialog(loginView.frame, "회원가입이 완료되었습니다.", "안내", loginView.diaNoti.INFORMATION_MESSAGE);
                }
            }
            //취소 버튼
            else if (obj == loginView.btn[1]) {
                loginView.diaJoin.dispose();
            }
            //중복확인 버튼
            else if (obj == loginView.btn[2]) {
                customer.setId(loginView.tfJoin[0].getText());
                if (customerDAO.idCheck()) {
                    loginView.tfJoin[0].setEnabled(false);
                    loginView.btn[0].setEnabled(true);
                    loginView.diaNoti.showMessageDialog(loginView.diaJoin, "회원 가입이 가능한 아이디입니다.", "안내",loginView.diaNoti.INFORMATION_MESSAGE );
                } else
                	loginView.diaNoti.showMessageDialog(loginView.diaJoin, "이미 있는 아이디입니다.", "안내",loginView.diaNoti.ERROR_MESSAGE );
            }
        } //actionPerformed()
    } //JoinActionL class

    //------------------------------------------------------------------------------------------------메인 뷰에서의 액션리스너
    class MainViewActionL implements ActionListener {

        // 어떤 버튼에서 액션이 일어났는지 알기위해 obj를 받아와 for문으로 비교해가며 찾음
        public boolean Istogglebtn (Object obj) {
            boolean flag = false;
            for(int j=0; j<50;j++) {
                if(obj==mainView.tBtn[j]) flag=true;
            }
            return flag;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object obj = e.getSource();

            // 영화패널 > 버튼
            if(obj == mainView.nextBtn) {
                mainView.mIdx++;
                // 영화 끝으로 도달했을때 다시 처음으로 돌리기
                if(mainView.mIdx == mainView.movies.size())
                    mainView.mIdx -= mainView.movies.size();

                // > 버튼 클릭시 영화 정보 변경
                mainView.ta[0].setText(mainView.movies.get(mainView.mIdx).getTitle());
                mainView.ta[1].setText("\n\n" + mainView.movies.get(mainView.mIdx).getGenre());
                mainView.ta[2].setText("\n\n" + apiMovie.getinfo(mainView.movies.get(mainView.mIdx).getTitle(), "actor"));
                mainView.infoLbl[3].setText("평점 : " + apiMovie.getinfo(mainView.movies.get(mainView.mIdx).getTitle(), "userRating"));

                curMovie = mainView.movies.get(mainView.mIdx);
            }

            // 영화패널 < 버튼
            else if(obj == mainView.preBtn){
                mainView.mIdx--;
                // 영화 처음에서 < 버튼 클릭시 마지막 영화로 돌리기
                if(mainView.mIdx == -1)
                    mainView.mIdx += mainView.movies.size();

                // < 버튼 클릭시 영화 정보 변경
                mainView.ta[0].setText(mainView.movies.get(mainView.mIdx).getTitle());
                mainView.ta[1].setText("\n\n" + mainView.movies.get(mainView.mIdx).getGenre());
                mainView.ta[2].setText("\n\n" + apiMovie.getinfo(mainView.movies.get(mainView.mIdx).getTitle(), "actor"));
                mainView.infoLbl[3].setText("평점 : " + apiMovie.getinfo(mainView.movies.get(mainView.mIdx).getTitle(), "userRating"));

                curMovie = mainView.movies.get(mainView.mIdx);
            }

            // 추천영화패널 > 버튼
            else if(obj == mainView.nextBtn2) {
                mainView.genreIdx++;
                // 영화 끝으로 도달했을때 다시 처음으로 돌리기
                if(mainView.genreIdx == mainView.genreMovies.size())
                    mainView.genreIdx -= mainView.genreMovies.size();

                // > 버튼 클릭시 영화 정보 변경
                mainView.ta2[0].setText(mainView.genreMovies.get(mainView.genreIdx).getTitle());
                mainView.ta2[1].setText("\n\n" + mainView.genreMovies.get(mainView.genreIdx).getGenre());
                mainView.ta2[2].setText("\n\n" + apiMovie.getinfo(mainView.genreMovies.get(mainView.genreIdx).getTitle(), "actor"));
                mainView.infoLbl2[3].setText("평점 : " + apiMovie.getinfo(mainView.genreMovies.get(mainView.genreIdx).getTitle(), "userRating"));

                curMovie = mainView.genreMovies.get(mainView.genreIdx);
            }

            // 추천영화패널 < 버튼
            else if(obj == mainView.preBtn2){
                mainView.genreIdx--;
                // 영화 끝으로 도달했을때 다시 처음으로 돌리기
                if(mainView.genreIdx == -1)
                    mainView.genreIdx += mainView.genreMovies.size();

                // < 버튼 클릭시 영화 정보 변경
                mainView.ta2[0].setText(mainView.genreMovies.get(mainView.genreIdx).getTitle());
                mainView.ta2[1].setText("\n\n" + mainView.genreMovies.get(mainView.genreIdx).getGenre());
                mainView.ta2[2].setText("\n\n" + apiMovie.getinfo(mainView.genreMovies.get(mainView.genreIdx).getTitle(), "actor"));
                mainView.infoLbl2[3].setText("평점 : " + apiMovie.getinfo(mainView.genreMovies.get(mainView.genreIdx).getTitle(), "userRating"));

                curMovie = mainView.genreMovies.get(mainView.genreIdx);
            }

            // 영화 탭 버튼 클릭
            else if(obj == mainView.btnMovie) {
                mainView.card.show(mainView.tab, "movie");
                curMovie = mainView.movies.get(mainView.mIdx);
            }
            // 영화 탭에서 예매하기 버튼 클릭시
            else if(obj == mainView.bookBtn) {
                mainView.card.show(mainView.tab, "book");
            }
            // 추천 영화 탭 클릭
            else if(obj == mainView.btnRecmov) {
                curMovie = mainView.genreMovies.get(mainView.genreIdx);
                mainView.card.show(mainView.tab, "recmovie");
            }
            // 추천영화 탭에서 예매하기 버튼 클릭시
            else if(obj == mainView.bookBtn2) {
                mainView.card.show(mainView.tab, "book");
            }
            // 예매 화면에서 선택 버튼 클릭 시
            else if(obj == mainView.btn_book[0]) {
                mainView.card.show(mainView.tab, "snack");
                String str=seatselect[0];
                for(int i=1;i<4;i++) {
                    if(seatselect[i]==null) continue;
                    str=str+","+seatselect[i];
                }
                ticket.setCutomername(customer.id);
                ticket.setSeletseat(str);
                ticket.setTotalnum((int)mainView.aduSpi.getValue()+(int)mainView.stuSpi.getValue());
                ticket.setTotalprice((int)mainView.aduSpi.getValue()*10000+(int)mainView.stuSpi.getValue()*7000);
                ticketDAO.newTicket(ticket); // 아이디,총명수,선택한 좌석들,총가격 DB에 저장
                mainView.currentPay.setText("현재 금액 : " + ticket.getTotalprice() + " 원");
                for (int j=0;j<seatselect.length;j++){
                    seatDAO.setSelectedSeat(seatselect[j]);
                }

                mainView.btnMovie.setEnabled(false);
                mainView.btnRecmov.setEnabled(false);
            }
            // 예매 화면에서 취소 버튼 클릭 시
            else if(obj == mainView.btn_book[1]) {
                mainView.card.show(mainView.tab, "movie");
                mainView.btn_book[0].setEnabled(false);
                mainView.aduSpi.setValue(0);
                mainView.stuSpi.setValue(0);
                for(int l=0;l<50;l++) {
                    mainView.tBtn[l].setSelected(false);
                    if(mainView.tBtn[l].getText().equals("X")) continue;
                    mainView.tBtn[l].setEnabled(true);
                }
            }
            // 매점 탭 클릭시
            else if(obj == mainView.btnSnack) {
                mainView.card.show(mainView.tab, "snack");
            }
            // 매점 탭에서 확인 버튼 클릭 시
            else if(obj == mainView.btn_snack[0]) {
                for(int i=0;i<4;i++){
                    if(!mainView.menu2[i].getValue().equals(0)){
                        pString = pString + mainView.pName[i] + " " + mainView.menu2[i].getValue() + "개 ";
                    }
                }
                mainView.infoL_pay[1].setText(pString); // 팝콘 문자열 부분 변경
                snackOrder.setPSnackID(pString);

                for(int i=0;i<4;i++){
                    if(!mainView.menu4[i].getValue().equals(0)) {
                        bString = bString + mainView.bName[i] + " " + mainView.menu4[i].getValue() + "개 ";
                    }
                }
                for(int i=0;i<4;i++){
                    if(!mainView.menu6[i].getValue().equals(0)) {
                        bString = bString + mainView.bName[i+4] + " " + mainView.menu6[i].getValue() + "개 ";
                    }
                }
                mainView.infoL_pay[2].setText(bString); // 음료 문자열 부분 변경
                snackOrder.setBSnackID(bString);

                // 매점 구입내역 DB에 저장
                snackOrderDAO.newSnackOrder();

                mainView.infoL_pay[3].setText("* 총 결제 금액 : " + String.valueOf(allPrice) + "원");
                mainView.infoL_pay[0].setText("티켓 수 : 성인 " + aultTiketnum +"명, 청소년 " + studentTiketnum +"명");
                mainView.card.show(mainView.tab, "pay");

                mainView.btnMovie.setEnabled(false);
                mainView.btnRecmov.setEnabled(false);
                mainView.btnSnack.setEnabled(false);
                mainView.btnPay.setEnabled(true);
            }
            // 매점 탭에서 건너뛰기 버튼 클릭 시
            else if(obj == mainView.btn_snack[1]) {
                mainView.infoL_pay[3].setText("* 총 결제 금액 : " + ticket.getTotalprice() + "원");
                mainView.infoL_pay[0].setText("티켓 수 : 성인 " + aultTiketnum +"명, 청소년 " + studentTiketnum +"명");
                mainView.card.show(mainView.tab, "pay");


            }
            // 결제 탭 클릭시
            else if(obj == mainView.btnPay) {
                mainView.card.show(mainView.tab, "pay");
            }
            // 결제 탭에서 결제하기 버튼 클릭 시
            else if(obj == mainView.btn_pay[0]) {
                mainView.diaSuc.setVisible(true);

                mainView.card.show(mainView.tab, "movie");

                // 탭을 초기 상태로 변경
                mainView.btnMovie.setEnabled(true);
                mainView.btnRecmov.setEnabled(true);
                mainView.btnSnack.setEnabled(true);
                mainView.btnPay.setEnabled(false);
            }
            // 결제 탭에서 취소 버튼 클릭 시
            else if(obj == mainView.btn_pay[1]) {
                mainView.card.show(mainView.tab, "movie");

                // 탭을 초기 상태로 변경
                mainView.btnMovie.setEnabled(true);
                mainView.btnRecmov.setEnabled(true);
                mainView.btnSnack.setEnabled(true);
                mainView.btnPay.setEnabled(false);
            }
            // 고객센터 탭 클릭시
            else if(obj == mainView.btnHelp) {
            	mainView.diaHelp.setVisible(true);
            }
            // 고객센터 탭에서 종료 버튼 클릭 시
            else if(obj == mainView.exitButton) {
            	mainView.diaHelp.dispose();
            }
            // 좌석 선택하는 좌석 버튼들
            else if(Istogglebtn(obj)) {
                int selectcount =0; //버튼을 선택 한 개수
                for(int i=0; i<50; i++) {
                    // 선택 될 때 마다 개수 증가
                    if(mainView.tBtn[i].isSelected()) {
                        seatselect[selectcount]=mainView.tBtn[i].getText();
                        selectcount++;
                    }
                    // 성인티켓 매수와 학생 티켓매수 합과 선택된 좌석 개수가 같을 때
                    else if(selectcount==(aultTiketnum+studentTiketnum)) {
                        mainView.btn_book[0].setEnabled(true);
                        mainView.stuSpi.setEnabled(false);
                        mainView.aduSpi.setEnabled(false);
                        for(int q=0; q<50; q++) {
                            if(!mainView.tBtn[q].isSelected()){
                                mainView.tBtn[q].setEnabled(false); // 모든 버튼 비활성화
                            }
                        }
                    }
                    else if (selectcount<(aultTiketnum+studentTiketnum)) {
                        mainView.btn_book[0].setEnabled(false);
                        mainView.stuSpi.setEnabled(true);
                        mainView.aduSpi.setEnabled(true);
                        for(int z=0;z<50;z++) {
                            if(!mainView.tBtn[z].isSelected()){
                                if(mainView.tBtn[z].getText().equals("X")) continue;
                                mainView.tBtn[z].setEnabled(true); // 모든 버튼 비활성화
                            }
                        }
                    }
                }
            } // togglebuttonAction

            // 결제 수단을 선택 했을 때 결제하기 버튼 활성화
            for(int i=0;i<3;i++){
                if(mainView.rb[i].isSelected()){
                    mainView.btn_pay[0].setEnabled(true);
                }
            }

            mainView.titLbl.setText(curMovie.getTitle());
            mainView.movieTitle1 = curMovie.getTitle();

            mainView.imgLbl.setIcon(new ImageIcon("src/com/company/img/" + mainView.movieTitle1 + ".jpg"));
            mainView.imgLbl2.setIcon(new ImageIcon("src/com/company/img/" + mainView.movieTitle1 + ".jpg"));

            mainView.imgL_pay.setIcon(new ImageIcon("src/com/company/img/" + curMovie.getTitle() + ".jpg"));
        }// actionPerformed()

    }// MainViewActionL class
    //------------------------------------------------------------------------------------매점 뷰에서 메뉴 갯수에 대한 가격 리스너
    class JspinnerChangeL implements ChangeListener {
        int hap =0;

        @Override
        public void stateChanged(ChangeEvent e) {
            Object obj = e.getSource();
            int allprice = ticket.getTotalprice();

            // 성인 예매티켓 개수 버튼
            if(obj == mainView.aduSpi) {
                int prevalue = (int)mainView.aduSpi.getPreviousValue();  // 티켓4개이상일때 올리기이전 값 저장
                hap=(int)mainView.stuSpi.getNextValue()+(int)mainView.aduSpi.getNextValue();
                aultTiketnum = (int)mainView.aduSpi.getValue();
                for(int j=0;j<50;j++) {
                    if(mainView.tBtn[j].getText().equals("X")) continue;
                    mainView.tBtn[j].setEnabled(true);
                }
                //0이하로 내려가지 않게 함
                if(mainView.aduSpi.getValue().equals(-1))
                    setZero(obj);
                    //총 티켓수가 4개일때
                else if(hap>6) {
                    mainView.aduSpi.setValue(prevalue);
                    mainView.stuSpi.setValue(studentTiketnum);
                }
                else if(mainView.aduSpi.getValue().equals(0) && mainView.stuSpi.getValue().equals(0)){
                    for(int i=0;i<50;i++) {
                        mainView.tBtn[i].setEnabled(false);
                    }
                }
            }

            // 학생 예매티켓 개수 버튼
            else if (obj == mainView.stuSpi) {
                int prevalue = (int)mainView.stuSpi.getPreviousValue();
                hap=(int)mainView.stuSpi.getNextValue()+(int)mainView.aduSpi.getNextValue();
                studentTiketnum = (int)mainView.stuSpi.getValue();
                for(int j=0;j<50;j++) {
                    if(mainView.tBtn[j].getText().equals("X")) continue;
                    mainView.tBtn[j].setEnabled(true);
                }
                //0이하로 내려가지 않게
                if(mainView.stuSpi.getValue().equals(-1))
                    setZero(obj);
                    //총 티켓 수가 4개 일때
                else if(hap>6) {
                    mainView.aduSpi.setValue(aultTiketnum);
                    mainView.stuSpi.setValue(prevalue);
                }
                else if(mainView.aduSpi.getValue().equals(0) && mainView.stuSpi.getValue().equals(0)) {
                    for (int i = 0; i < 50; i++) {
                        mainView.tBtn[i].setEnabled(false);
                    }
                }
            }

            // 고소팝콘M 개수 버튼
            else if(obj == mainView.menu2[0]) {
                // 0일때 더 낮게 선택 못함
                if(mainView.menu2[0].getValue().equals(-1)){
                    setZero(obj);
                }
                sum[0] = snackDAO.getPrice("고소팝콘M",(int)mainView.menu2[0].getValue());
            }

            // 고소팝콘L 개수 버튼
            else if (obj == mainView.menu2[1]) {
                // 0일때 더 낮게 선택 못함
                if(mainView.menu2[1].getValue().equals(-1))
                    setZero(obj);

                sum[1] = snackDAO.getPrice("고소팝콘L",(int)mainView.menu2[1].getValue());
            }

            // 달콤팝콘M 개수 버튼
            else if (obj == mainView.menu2[2]) {
                // 0일때 더 낮게 선택 못함
                if(mainView.menu2[2].getValue().equals(-1))
                    setZero(obj);

                sum[2] = snackDAO.getPrice("달콤팝콘M",(int)mainView.menu2[2].getValue());
            }

            // 달콤팝콘L 개수 버튼
            else if (obj == mainView.menu2[3]) {
                // 0일때 더 낮게 선택 못함
                if(mainView.menu2[3].getValue().equals(-1))
                    setZero(obj);

                sum[3] = snackDAO.getPrice("달콤팝콘L",(int)mainView.menu2[3].getValue());
            }

            // 콜라M 개수 버튼
            else if (obj == mainView.menu4[0]) {
                // 0일때 더 낮게 선택 못함
                if(mainView.menu4[0].getValue().equals(-1))
                    setZero(obj);

                sum[4] = snackDAO.getPrice("콜라M",(int)mainView.menu4[0].getValue());
            }

            // 콜라L 개수 버튼
            else if (obj == mainView.menu4[1]) {
                // 0일때 더 낮게 선택 못함
                if(mainView.menu4[1].getValue().equals(-1))
                    setZero(obj);

                sum[5] = snackDAO.getPrice("콜라L",(int)mainView.menu4[1].getValue());
            }

            // 사이다M 개수 버튼
            else if (obj == mainView.menu4[2]) {
                // 0일때 더 낮게 선택 못함
                if(mainView.menu4[2].getValue().equals(-1))
                    setZero(obj);

                sum[6] = snackDAO.getPrice("사이다M",(int)mainView.menu4[2].getValue());
            }

            // 사이다L 개수 버튼
            else if (obj == mainView.menu4[3]) {
                // 0일때 더 낮게 선택 못함
                if(mainView.menu4[3].getValue().equals(-1))
                    setZero(obj);

                sum[7] = snackDAO.getPrice("사이다L",(int)mainView.menu4[3].getValue());
            }

            // 자몽에이드M 개수 버튼
            else if (obj == mainView.menu6[0]) {
                // 0일때 더 낮게 선택 못함
                if(mainView.menu6[0].getValue().equals(-1))
                    setZero(obj);

                sum[8] = snackDAO.getPrice("자몽에이드M",(int)mainView.menu6[0].getValue());
            }

            // 자몽에이드L 개수 버튼
            else if (obj == mainView.menu6[1]) {
                // 0일때 더 낮게 선택 못함
                if(mainView.menu6[1].getValue().equals(-1))
                    setZero(obj);

                sum[9] = snackDAO.getPrice("자몽에이드L",(int)mainView.menu6[1].getValue());
            }

            // 오렌지에이드M 개수 버튼
            else if (obj == mainView.menu6[2]) {
                // 0일때 더 낮게 선택 못함
                if(mainView.menu6[2].getValue().equals(-1))
                    setZero(obj);

                sum[10] = snackDAO.getPrice("오렌지에이드M",(int)mainView.menu6[2].getValue());
            }

            // 오렌지에이드L 개수 버튼
            else if (obj == mainView.menu6[3]) {
                // 0일때 더 낮게 선택 못함
                if(mainView.menu6[3].getValue().equals(-1))
                    setZero(obj);

                sum[11] = snackDAO.getPrice("오렌지에이드L",(int)mainView.menu6[3].getValue());
            }

            // 현재 선택한 메뉴의 총 가격
            for(int i=0;i<12;i++) allprice = allprice + sum[i];

            // 1개 이상 메뉴 선택시 확인 버튼 활성화
            if(allprice > 0 )
                mainView.btn_snack[0].setEnabled(true);
            else
                mainView.btn_snack[0].setEnabled(false);


            mainView.currentPay.setText("현재 금액 : " + allprice + " 원");
            allPrice = allprice;

        }
    }//JspinnerChangeL class

    // 회원가입시 공백 체크
    public boolean blankCheck(){
        boolean flag = true;

        // 모든 텍스트필드가 공백이 아니라면
        for(int i=0;i<3;i++) {
            if(loginView.tfJoin[i+1].getText().equals("")){
                flag = false;
                break;
            }
        }

        // 텍스트 필드가 모두 작성되었다면
        if(flag) {
            flag = false;
            for (int i = 0; i < 6; i++) {
                if (loginView.rb[i].isSelected()) {
                    flag = true;
                    break;
                }
            }
        }

        return flag;
    }

    // 0보다 더 낮게 선택 못함
    public void setZero(Object menu){
        JSpinner js = (JSpinner)menu;
        if(js.getValue().equals(-1)){
            js.setValue(0);
        }
    }

    public void LoginListenerSet() {
        loginView.addListenerLogin(lL);
    } //ListenerSet()
    public void JoinListenerSet() {
        loginView.addLisenerJoin(jL);
    }
    public void MainViewListenerSet() {
        mainView.addListenerMain(mL);
    }
    public void JSpinnerChangeListenerSet() {
        mainView.addChangeListenerMain(cL);
    }
}//MyActionListener class
