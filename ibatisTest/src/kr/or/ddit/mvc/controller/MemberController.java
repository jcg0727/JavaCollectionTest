package kr.or.ddit.mvc.controller;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import kr.or.ddit.mvc.service.IMemberService;
import kr.or.ddit.mvc.service.MemberServiceImpl;
import kr.or.ddit.mvc.vo.MemberVO;
import kr.or.ddit.util.CryptoUtil;
import kr.or.ddit.util.DBUtil;

public class MemberController {
   private Scanner scan = new Scanner(System.in);
   private IMemberService service;
   private String key = "asdqwe123asdqwe123";
   private String enc;
   
   
   public MemberController() {
     // service = new MemberServiceImpl();
	   service = MemberServiceImpl.getInstance();
   }
   
   public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
      new MemberController().memberStart();
   }
   private void memberStart() throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
         while (true) {
            int choice = displayMenu();
            switch (choice) {
            case 1:
               insertMember();
               break;
            case 2:
               deleteMember();
               break;
            case 3:
               updateMember();
               break;
            case 4:
               displayAllMember();
               break;
            case 5:   // 수정 2
               updateMember2();
               break;
            case 0:
               System.out.println("프로그램을 종료합니다..");
               return;

            default:
               System.out.println("번호를 잘못 입력했습니다.");
               System.out.println("다시 입력하세요.");
            }
         }
      }
   
   //pdf로 출력할 때도 표형태로 출력하도록 한다.
   // 전체자료 Excel로 출력
   // 전체 자료 PDF로 출력
      // 메뉴를 출력하고 실행할 작업번호를 입력받아 반환하는 메서드
      private int displayMenu() {
         System.out.println("========== 작업선택 ==========");
         System.out.println("1. 자료 추가");
         System.out.println("2. 자료 삭제");
         System.out.println("3. 자료 수정");
         System.out.println("4. 전체 자료 출력");
         System.out.println("5. 자료 수정2");
         System.out.println("0. 작업 끝");
         System.out.println("=============================");
         System.out.print("작업 선택 >> ");
         return scan.nextInt();
      }   
      
      // 회원 정보를 추가하는 메서드
      private void insertMember() throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException { 
    	  
         System.out.println();
         System.out.println("추가할 회원 정보를 입력하세요.");
         int count = 0;  // 중복 여부를 검사하기 위한 변수 (ID개수가 저장될 변수)
         String memId = null;
         
         do {
            System.out.print("회원ID >> ");
            memId = scan.next();
            count = service.getMemberCount(memId);
            enc = CryptoUtil.encryptAED256(memId, key);
            
            if(count>0) {
               System.out.print(memId + "은(는) 이미 등록된 회원ID 입니다.");
               System.out.println("다른 회원ID를 입력하세요.");
            }
                  
         } while (count>0);
         
         System.out.print("패스워드 >> ");
         String memPass = scan.next();
         
         String sha512 = CryptoUtil.sha512(memPass);
         
         
         System.out.print("회원이름 >> ");
         String memName = scan.next();
         
         System.out.print("전화번호 >> ");
         String memTel = scan.next();
         
         scan.nextLine();  // 입력 버퍼 비우기
         System.out.print("회원주소 >> ");
         String memAddr = scan.nextLine();
         
         // 입력한 데이터가 저장될 VO객체 생성
         MemberVO memVo = new MemberVO();
         
         // 입력한 데이터를 VO에 저장한다.
         memVo.setMem_id(enc);
         memVo.setMem_name(memName);
         memVo.setMem_pass(sha512);
         memVo.setMem_tel(memTel);
         memVo.setMem_addr(memAddr);
         
         int cnt = service.insertMember(memVo);
         if(cnt>0) {
            System.out.println("회원 추가 성공!!!");
         }else {
            System.out.println("회원 추가 실패~~");
         }
    
      }
      
      // 삭제 메서드
      private void deleteMember() {
         
         System.out.println();
         System.out.println("삭제할 회원 정보를 입력하세요.");
         System.out.print("삭제할 회원ID >> ");
         String memId = scan.next();
         
         int cnt = service.deleteMember(memId);
         
         if(cnt>0) {
            System.out.println("회원 삭제 성공!!!");
         }else {
            System.out.println("회원은 없는 회원이거나 삭제에 실패했습니다.");
         }
         
      }
      
      // 회원 정보를 수정하는 메서드
      private void updateMember() {
         
         System.out.println("수정할 회원 정보를 입력하세요.");
         System.out.print("회원ID >> ");
         String memId = null;
         
         memId = scan.next();
            
         int count = service.getMemberCount(memId);
            
         if(count == 0) {  // 없는 회원ID를 입력했을 경우
         System.out.println(memId + "은(는) 없는 회원ID 입니다.");
         System.out.println("수정 작업을 종료합니다.");
         return;
         }
         
         System.out.println("수정할 내용을 입력하세요.");
         System.out.print("새로운 패스워드 >> ");
         String memPass = scan.next();
            
         System.out.print("새로운 회원이름 >> ");
         String memName = scan.next();
            
         System.out.print("새로운 전화번호 >> ");
         String memTel = scan.next();
            
         scan.nextLine();
         System.out.print("새로운 주소 >> ");
         String memAddr = scan.nextLine();   
         
         MemberVO memVo = new MemberVO();
         
         memVo.setMem_id(memId);
         memVo.setMem_pass(memPass);
         memVo.setMem_name(memName);
         memVo.setMem_tel(memTel);
         memVo.setMem_addr(memAddr);
         
         int cnt = service.updateMember(memVo);
         
         if(cnt>0) {
               System.out.println("수정 성공!!!");
         }else {
               System.out.println("수정 실패~~");
         }   
        
         
         
      }
      
      private void displayAllMember() throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{
         List<MemberVO> memList = service.getAllMemberList();
         
         System.out.println();
         System.out.println("-----------------------------------");
         System.out.println(" ID   PASSWORD  이 름     전화번호    주  소");
         System.out.println("-----------------------------------");
         
         if(memList==null || memList.size() == 0) {
            System.out.println("출력할 데이터가 하나도 없습니다.");
         }else {
            for(MemberVO memvo : memList) {
               System.out.println(memvo.getMem_id() + "\t" +
                     memvo.getMem_pass() + "\t" + 
                     memvo.getMem_name() + "\t" +
                     memvo.getMem_tel() + "\t" +
                     memvo.getMem_addr());
            }
            
         }
         
         
//         for(int i=0; i<memList.size(); i++) {
//            System.out.println(memList.get(i));
//         }
         
      }
      
      
      private void updateMember2() {
         
         System.out.println("수정할 회원 정보를 입력하세요.");
         System.out.print("회원ID >> ");
         String memId = scan.next();
         
         int count = service.getMemberCount(memId);
         
         if(count == 0) {  // 없는 회원ID를 입력했을 경우
         System.out.println(memId + "은(는) 없는 회원ID 입니다.");
         System.out.println("수정 작업을 종료합니다.");
         return;
         }
         
          int num;  // 선택한 항목의 번호가 저장될 변수 
            String updateField = null;  // 수정할 컬럼명이 저장될 변수
            String updateTitle = null;  
            do {
               System.out.println();
               System.out.println("수정할 항목을 선택하세요.");
               System.out.println("1.비밀번호  2.회원이름  3.전화번호  4.회원주소");
               System.out.println("-----------------------------------");
               System.out.print("수정 항목 선택 >> ");
               num = scan.nextInt();
               
               switch(num) {
                     case 1 : updateField = "mem_pass"; updateTitle = "비밀번호";
                           break;
                     case 2 : updateField = "mem_name"; updateTitle = "회원이름";
                           break;
                     case 3 : updateField = "mem_tel"; updateTitle = "전화번호";
                           break;
                     case 4 : updateField = "mem_addr"; updateTitle = "회원주소";
                           break;
                     default : 
                        System.out.println("수정할 항목을 잘못 선택했습니다.");
                        System.out.println("다시 선택하세요.");
               }
               
            }while(num<1 || num>4);
            
            System.out.println();
            scan.nextLine();  // 입력 버퍼 비우기
            System.out.print("새로운 " + updateTitle + " >> ");
            String updateData = scan.nextLine();
            
            // 선택한 컬럼명, 입력한 데이터, 회원ID를 Map객체에 저장한다.
            
            //  Key값 정보 ==> 회원ID(memId), 수정할컬럼명(field), 수정할데이터(data)
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("memId", memId);
            paramMap.put("field", updateField);
            paramMap.put("data", updateData);
            
            int cnt = service.memberUpdate2(paramMap);
            
            if(cnt>0) {
               System.out.println("수정 작업 성공~~");
            }else {
               System.out.println("수정 작업 실패!!!");
            }
            
      }
      
      /*
      private void updateMember2() {
         
         System.out.println("수정할 회원 정보를 입력하세요.");
         System.out.print("회원ID >> ");
         String memId = scan.next();
         
         int count = service.getMemberCount(memId);
         
         if(count == 0) {  // 없는 회원ID를 입력했을 경우
         System.out.println(memId + "은(는) 없는 회원ID 입니다.");
         System.out.println("수정 작업을 종료합니다.");
         return;
         }
         
          int num;  // 선택한 항목의 번호가 저장될 변수 
            String updateField = null;  // 수정할 컬럼명이 저장될 변수
            String updateTitle = null;  
            do {
               System.out.println();
               System.out.println("수정할 항목을 선택하세요.");
               System.out.println("1.비밀번호  2.회원이름  3.전화번호  4.회원주소");
               System.out.println("-----------------------------------");
               System.out.print("수정 항목 선택 >> ");
               num = scan.nextInt();
               
               switch(num) {
                     case 1 : updateField = "mem_pass"; updateTitle = "비밀번호";
                           break;
                     case 2 : updateField = "mem_name"; updateTitle = "회원이름";
                           break;
                     case 3 : updateField = "mem_tel"; updateTitle = "전화번호";
                           break;
                     case 4 : updateField = "mem_addr"; updateTitle = "회원주소";
                           break;
                     default : 
                        System.out.println("수정할 항목을 잘못 선택했습니다.");
                        System.out.println("다시 선택하세요.");
               }
               
            }while(num<1 || num>4);
            
            System.out.println();
            scan.nextLine();  // 입력 버퍼 비우기
            System.out.print("새로운 " + updateTitle + " >> ");
            String updateData = scan.nextLine();
            
            int cnt = service.updateMember2(memId, updateField, updateData);
            
            if(cnt>0) {
               System.out.println("수정 작업 성공~~");
            }else {
               System.out.println("수정 작업 실패!!!");
            }
            
      }
       */
      
}