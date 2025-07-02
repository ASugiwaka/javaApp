package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import bean.UserBean;

/**
 * 利用者表アクセス用DAO
 * 
 * @author 大村
 */
public class UserDao {
	private static final String URL = "jdbc:mysql://127.0.0.1:3306/meetingroomA";
	private static final String USER = "user";
	private static final String PASSWORD = "pass";

	/**
	 * JDBCドライバをロード
	 * データベース呼び出し・接続
	 */
	// JDBCドライバのロード add Sugiwaka
	public static void loadJDBCDriver() {
		try {

			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// ドライバが見つからない場合の例外処理
			e.printStackTrace();
		}
	}

	/**
	 * 利用者IDとパスワードで利用者認証を行い，認証した利用者情報を返します。
	 * @param id - String 利用者ID
	 * @param password - String パスワード
	 * @return UserBean 認証に成功した場合は利用者，それ以外の場合null
	 */
	public static UserBean certificate(String id, String password) {
		String sql = "SELECT id, password, name, address FROM user WHERE id = ? AND password = ?";

		// JDBCドライバのロード add Sugiwaka ----		
		loadJDBCDriver();
		// ----

		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			String hashPass = HashUtil.sha256(password); //暗号化 Sugiwaka
			stmt.setString(1, id);
			stmt.setString(2, hashPass);

			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				return new UserBean(
						rs.getString("address"),
						rs.getString("id"),
						rs.getString("name"),
						rs.getString("password"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	
	/**
	 * ユーザーIDからユーザーの名前を取得する add Sugiwaka
	 * @param userId - String 利用者ID
	 * @return userName(存在しない場合はnull）
	 */
	public static String userName(String userId) {
		String sql = "SELECT name FROM user WHERE id = '" + userId + "'";
		loadJDBCDriver();

		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				return rs.getString("name");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
