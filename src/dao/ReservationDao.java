package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import bean.ReservationBean;

/**
 * 予約表アクセス用DAO
 * 
 * @author 大村
 */
public class ReservationDao {

	private static final String URL = "jdbc:mysql://127.0.0.1:3306/meetingroomA";
	private static final String USER = "user";
	private static final String PASSWORD = "pass";

	/**
	 * JDBCドライバをロード
	 * データベース呼び出す・接続
	 */
	// JDBCドライバのロード add wkSugiwakaa
	public static void loadJDBCDriver() {
		try {

			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// ドライバが見つからない場合の例外処理
			e.printStackTrace();
		}
	}

	/**
	 * 利用日の予約を検索します。
	 * @param reservation - ReservationBean 予約情報
	 * @return boolean 予約できた場合はtrue，それ以外の場合はfalse
	 */
	// 予約
	public static boolean insert(ReservationBean reservation) {
		String sql = "INSERT INTO reservation (roomId, date, start, end, userId) VALUES (?, ?, ?, ?, ?)";
		loadJDBCDriver(); //add Sugiwaka

		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, reservation.getRoomId());
			ps.setString(2, reservation.getDate());
			ps.setString(3, reservation.getStart());
			ps.setString(4, reservation.getEnd());
			ps.setString(5, reservation.getUserId());
			int result = ps.executeUpdate();
			return result > 0;

		} catch (Exception e) {
			e.printStackTrace();
			// return false;
		}

		return false;
	}

	/**
	 * 予約を削除します。
	 * @param reservation - ReservationBean 予約情報
	 * @return boolean 予約をキャンセルできた場合はtrue，それ以外の場合はfalse
	 */
	// キャンセル
	public static boolean delete(ReservationBean reservation) {
		//String sql = "DELET FROM reservation WHERE date = ? AND userId = ? AND roomId = ?";//del Sugiwaka
		String sql = "DELETE FROM reservation WHERE roomId = ? AND date = ? AND start = ?"; //add Sugiwaka
		loadJDBCDriver(); //add Sugiwaka

		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement ps = conn.prepareStatement(sql)) {
			// del Sugiwaka ----
			//ps.setString(1, reservation.getDate());
			//ps.setString(2, reservation.getUserId());
			//ps.setString(3, reservation.getRoomId());
			// ----
			ps.setString(1, reservation.getRoomId());
			ps.setString(2, reservation.getDate());
			ps.setString(3, reservation.getStart());
			int result = ps.executeUpdate();
			return result > 0;

		} catch (Exception e) {
			e.printStackTrace();
			//return false;
		}
		return false;
	}

	/**
	 * 予約の存在確認
	 * @param reservation - ReservationBean 予約情報 
	 * @return boolean 予約の存在確認ができた場合はtrue，それ以外の場合はfalse
	 */
	// 予約の存在確認  add Sugiwaka
	public static boolean findReservation(ReservationBean reservation) {
		String sql = "SELECT * FROM reservation WHERE date = ? AND start = ? AND roomId = ?";
		loadJDBCDriver(); //add Sugiwaka

		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, reservation.getDate());
			ps.setString(2, reservation.getStart());
			ps.setString(3, reservation.getRoomId());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				return true;
			}
			return false;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 予約IDとユーザIDの取得
	 * @param reservation - ReservationBean}
	 * @return ユーザーID,該当する予約がない場合は空文字列。
	 */
	public static String findReservationId(ReservationBean reservation) {
		String sql = "SELECT id, userId FROM reservation WHERE date = ?  AND start = ? AND roomId = ?";
		loadJDBCDriver(); //add Sugiwaka

		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, reservation.getDate());
			ps.setString(2, reservation.getStart());
			ps.setString(3, reservation.getRoomId());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				reservation.setId(rs.getInt("id"));
				return rs.getString("userId");
			}
			return "";

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * 指定日の全予約の取得
	 * @param date 検索する (YYYY-MM-DD形式)
	 * @return ReservationBean 予約情報のリスト予約がない場合はnull
	 */
	// 指定日の全予約の取得
	public static List<ReservationBean> findByDate(String date) {
		List<ReservationBean> list = new ArrayList<>();
		String sql = "SELECT * FROM reservation WHERE date = ?";
		loadJDBCDriver(); //add Sugiwaka

		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, date);
			ResultSet rs = ps.executeQuery();
			SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm"); // add Sugiwaka

			while (rs.next()) {
				String formattedStart = timeFormat.format(rs.getTime("start")); // add Sugiwaka
				String formattedEnd = timeFormat.format(rs.getTime("end")); // add Sugiwaka
				ReservationBean rb = new ReservationBean(
						rs.getInt("id"),
						rs.getString("roomId"),
						rs.getString("date"),
						formattedStart,
						formattedEnd,
						rs.getString("userId"));
				list.add(rb);
			}

			return list.isEmpty() ? null : list;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
