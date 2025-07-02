package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bean.RoomBean;
/**
 * 会議室表アクセス用DAO
 * @author 大村
 */
public class RoomDao {
	private static final String URL = "jdbc:mysql://127.0.0.1:3306/meetingroomA";
	private static final String USER = "user";
	private static final String PASSWORD = "pass";

/** 
 * すべての会議室を検索す
 * @return RoomBean[] 会議室の配列（見つからない場合は、nullを返却）
 */
	public static RoomBean[] findAll() {
		//String sql = "SELECT id, name FROM room"; // del Sugiwaka
		String sql = "SELECT id, name FROM room order by id"; //add Sugiwaka
		
		// JDBCドライバのロード add Sugiwaka ----		
		try {
			
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// ドライバが見つからない場合の例外処理
			e.printStackTrace();
		}		
		// ----
		
		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			//RoomBean roomBean = new RoomBean();
			// 会議室のリストを保持するためにListを使用
			List<RoomBean> roomList = new ArrayList<>();

			while (rs.next()) {
				String id = rs.getString("id");
				String name = rs.getString("name");
				//System.out.println(id);
				//System.out.println(name);
				roomList.add(new RoomBean(id, name));
			}
		
			// 会議室が1つでもあれば、配列に変換して返す
			if (roomList.isEmpty()) {
				return roomList.toArray(new RoomBean[0]);
			} else {
				return roomList.toArray(new RoomBean[roomList.size()]);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
