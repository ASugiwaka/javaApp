package bean;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import dao.ReservationDao;
import dao.RoomDao;
import dao.UserDao;
/** 
 * 会議室管理システムのモデル
 * @author 杉若
 */
public class MeetingRoom implements Serializable {
	//フィールド-----------------------------------------
	/** 直列化用バージョン番号 */
	private static final long serialVersionUID = 1L; //バージョン番号
	/** 利用日 */
	private String date;
	/** 利用時間(分)60分とする */
	private static final int INTERVAL = 60; //分（会議室の一コマ時間）
	/** 利用時間帯(開始時刻) ("09:00", "10:00", "11:00", "12:00","13:00", "14:00", "15:00", "16:00") */
	private static final String[] PERIOD = {"09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00"};
	/** 会議室 */
	private RoomBean[] rooms;
	/** 利用者 */
	private UserBean user;

	// コンストラクタ-----------------------------------------
	/** 
	 * 生成 会議室予約システムを初期化します。 
	 * 会議室の一覧を読込み，利用日を本日の日付で初期化します。
	 */
	public MeetingRoom() {
		LocalDate today = LocalDate.now(); // 今日の日付を取得
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.JAPAN);
		this.date = today.format(formatter);	
		this.rooms = RoomDao.findAll();
		this.user = new UserBean();
		
	}
	
	// メソッド-----------------------------------------
	
	/** 
	 * roomIdの会議室が配列に格納されている添字を返します。
	 * @param roomId - String 会議室ID
	 * @return int 配列の添字
	 * @throws IndexOutOfBoundsException - 会議室が存在しない場合
	 */
	public int roomIndex(String roomId) throws IndexOutOfBoundsException {
		for (int i = 0; i < rooms.length; i++) {
			if (rooms[i].getId().equals(roomId)) {
				return i;
			}
		}
		throw new IndexOutOfBoundsException("配列が範囲外です");
	}

	
	/**
	 * 利用開始時刻に対応する利用時間帯の添え字を計算します。
	 * @param start - String 利用開始時刻
	 * @return int 時間帯番号
	 * @throws IndexOutOfBoundsException - 利用時間帯の範囲外
	 */
	public int StartPeriod(String start) throws IndexOutOfBoundsException {
		//System.out.println("StartPeriod " + start);
		for (int i = 0; i < PERIOD.length; i++) {
			if (PERIOD[i].equals(start)) {
				return i;
			}
		}
		throw new IndexOutOfBoundsException("配列が範囲外です");
	}

	
	/** 
	 * 利用時間帯の配列を返す。
	 * @return String[] 開始時刻の配列
	 */
	public static String[] getPeriod() {
		return PERIOD;
	}

	/**
	 * 会議室予約システムで利用できるすべての会議室を返します。
	 * @return RoomBean[] 会議室の配列
	 */
	public RoomBean[] getRooms() {
		return rooms;
	}

	/**
	 * 利用会議室取得<br>会議室IDがroomIdの会議室を返します。
	 * @param roomId - String 会議室ID
	 * @return RoomBean 会議室(見つからない場合は、nullを返却)
	 */
	public RoomBean getRoom(String roomId) {
		for (RoomBean rb : rooms) {
			if (rb.getId().equals(roomId)) {
				return rb;
			}
		}		
		return null;
	}

	/**
	 * 会議室予約システムにログインしている利用者を返します。
	 * @return UserBean 利用者
	 */
	public UserBean getUser() {
		return user;
	}

	/**
	 * 利用時間(分)（定数）を返します
	 * @return INTERVAL
	 */
	public static int getInterval() {
		return INTERVAL;
	}

	/**
	 * 会議室予約システムの利用日を返します。
	 * @return String 利用日
	 */
	public String getDate() {
		return date;
	}

	/**
	 * 会議室予約システムの利用日を設定します。
	 * @param date - 利用日
	 */
	public void setDate(String date) {
		this.date = date;
	}


	/**
	 * 認証<br>会議室予約システムにログインします。
	 * @param id- String 利用者ID
	 * @param password - String パスワード
	 * @return ログインできた場合はtrue，それ以外の場合falseを返す
	 */
	public boolean login​(String id, String password) {
		user = UserDao.certificate(id, password);

		if (user != null) {
			return true;
		} else {
			return false;
		}

	}

	
	//
	/**
	 * 会議室予約システムの利用日における予約状況を返します。
	 * @return ReservationBean[][] 会議室，時間帯ごとの予約状況
	 */
	public ReservationBean[][] getReservations() {
		List<ReservationBean> list = ReservationDao.findByDate(date);

		ReservationBean[][] result = new ReservationBean[rooms.length][PERIOD.length];
		
		if (list == null) {
			return result;
		}
		for (ReservationBean reservation : list) {
			int rIndex = roomIndex(reservation.getRoomId());
			int sIndex = StartPeriod(reservation.getStart());

			result[rIndex][sIndex] = reservation;
		}

		return result;
	}


	/**
	 * 予約作成<br>start時間からend時間を計算して登録します。
	 * @param roomId - String 会議室ID
	 * @param start - String 利用開始時刻(HH:mm形式で受け取る事を想定)
	 * @return ReservationBean 会議室予約情報
	 */
	public ReservationBean createReservation(String roomId, String start) {

        // HH:mm形式のDateTimeFormatterを作成
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        // 入力時刻をLocalTime型に変換
        LocalTime time = LocalTime.parse(start, formatter);
        // 分を加算
        LocalTime newTime = time.plusMinutes(INTERVAL);
        // 終了時間
        String end = newTime.format(formatter);

        //予約IDなしのコンストラクタがあるよ♪
		return new ReservationBean(roomId, this.date,  start,  end, user.getId());
	}


	/**
	 * 予約登録<br>会議室予約情報で会議室Daoを利用し、予約します。
	 * @param reservation - ReservationBean 会議室予約情報
	 * @throws Exception - 予約ができない場合に次のメッセージの例外を投げます。
	 * <br>予約済みの場合："既に予約されています"
	 * <br>現在時刻が予約時間を過ぎている場合："時刻が過ぎているため予約できません"
	 */
	public void reserve​(ReservationBean reservation) throws Exception {
		// 現在時刻が予約時間を過ぎている場合："時刻が過ぎているため予約できません"
		String message = "";
		
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
		LocalDate localDate = LocalDate.parse(reservation.getDate(), dateFormatter);
		LocalTime localTime = LocalTime.parse(reservation.getStart(), timeFormatter);
		LocalDateTime reservationDateTime = LocalDateTime.of(localDate, localTime);

		// 現在の日時を取得
		LocalDateTime now = LocalDateTime.now();
		// 予約時間を経過していたら
		if (now.isAfter(reservationDateTime)) {
			message = "時刻が過ぎているため予約できません";
			throw new Exception(message);
		}

		// 予約がある？　→DaoにfindReservationメソッド追加してもらう
		if (ReservationDao.findReservation(reservation)){
			// 予約済みだったら
			message = "既に予約されています";
			throw new Exception(message);
		}
		
		// 予約処理
		if(ReservationDao.insert(reservation)) {
			ReservationDao.findReservationId(reservation);
		} else {
			message = "予約処理エラー";
			throw new Exception(message);
		};

	}


	/**
	 * 予約キャンセル<br>会議室予約情報で会議室をキャンセルします。
	 * @param reservation - ReservationBean 会議室予約情報
	 * @throws Exception キャンセルができない場合に次のメッセージの例外を投げます。
	 *  キャンセル済みの場合："既にキャンセルされています" 
	 *  現在時刻が予約時間を過ぎている場合："時刻が過ぎているためキャンセルできません"
	 */
	public void cancel​(ReservationBean reservation) throws Exception {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
		LocalDate localDate = LocalDate.parse(reservation.getDate(), dateFormatter);
		LocalTime localTime = LocalTime.parse(reservation.getStart(), timeFormatter);
		LocalDateTime reservationDateTime = LocalDateTime.of(localDate, localTime);

		// 現在の日時を取得
		LocalDateTime now = LocalDateTime.now();
		// 予約時間を経過していたら
		if (now.isAfter(reservationDateTime)) {
			
			throw new Exception("時刻が過ぎているためキャンセルできません");
		}
		
		// 予約があるかどうか確認
		String reserveId = ReservationDao.findReservationId(reservation);
		String nowId = reservation.getUserId();
		
		if (reserveId == "") {
			throw new Exception("既にキャンセルされています");
		}
		
		// 自分が作った予約なら削除
		if (reserveId.equals(nowId)) {
			ReservationDao.delete(reservation);
		}else {
			throw new Exception("あなたの予約ではありません");	
		}
	}

	/**
	 * このオブジェクトの文字列表現を返します。<br>デバッグ用
	 * @return String 会議室予約システムの文字列表現
	 */
	@Override
	public String toString() {
		return "MeetingRoom [date=" + date + ", rooms=" + Arrays.toString(rooms) + ", user=" + user + "]";
	}

}
