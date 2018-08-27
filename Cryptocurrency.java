import java.lang.*;
import java.sql.*;
import java.util.*;
//1

//1
public class Cryptocurrency {
	public static int MAX = 100000;// максимальное количество криптомонет, после этого майнинг валюты невозможен и
									// она постепенно выходит из обихода;
	private long stock; // фонд криптовалюты, влияет на цену (минимальный начальный 100к)
	private int arc_course; // цена криптовалюты, варьируется с помощью специальных методов
	private double comission;// процент от транзакции, не больше 99%
	private double req;// доступность майнинга, обратное значение к мощности для майнинга
						// 0.00001 части криптовалюты.
	// требования мощности возрастают с количеством добытых монет и немного зависит
	// от цены и отношения сумм к количествам транзакций;
	private int transactions;// количество транзакций
	private int summ;// общая сумма денежного оборота;
	private String name; // название
	private long owner; // id владельца
	private double use;// криптовалюта в обиходе
	private byte flag;// состояние криптовалюты ( (-1) - живая , 1 - умирающая);

	private Cryptocurrency(double comission, long stock, String name) {
		if ((comission <= 0) || (comission >= 1))
			throw new IllegalArgumentException();
		this.name = name;
		this.comission = comission;
		this.transactions = 0;
		this.summ = 0;
		this.stock = stock;
		this.arc_course = (int) (stock / 1000 + Math.random() * 1000); // начальный курс
		this.req = 1 / (comission * arc_course);
		this.use = 0;
		this.flag = -1;
	}

	private Cryptocurrency(String name, long stock, int arc_course, double comission, double req, int transactions,
			int summ, long owner, double use, byte flag) {
		this.name = name;
		this.comission = comission;
		this.transactions = transactions;
		this.summ = summ;
		this.stock = stock;
		this.arc_course = arc_course; // начальный курс
		this.req = req;
		this.owner = owner;
		this.flag = flag;
		this.use = use;
	}

	public byte getFlag() {
		return flag;
	}

	public double getUse() {
		return this.use;
	}

	public String getName() {
		return this.name;
	}

	public int getTransact() {
		return this.transactions;
	}

	public long getOwner() {
		return this.owner;
	}

	public int getSumm() {
		return this.summ;
	}

	public double getComission() {
		return this.comission;
	}

	public double getReq() {
		return this.req;
	}

	public long getStock() {
		return this.stock;
	}

	public int getCourse() {
		return this.arc_course;
	}

	public void setStock(long stock) {
		this.stock = stock;
	}

	public String toString() {
		return ("💬 Подробная информация для " + this.name + ";\n 💵 Kомиссия составляет" + this.comission * 100
				+ "%;\n 📈 Всего транзакций со времени создания:" + this.transactions + "; \n 📊 Pазмер фонда : " + this.stock
				+ "arkcoin`s;\n 💳 Курс:" + this.arc_course + "\n 📡 Минимальная мощность майнинга: "
				+ (Math.round(1 / this.getReq())) + ";\n 🤵 Владелец: vk.com/id" + this.getOwner());
	}

	public static void CryptoInfo() {
		Main.print("💬 Kриптовалюты - это симулятивное дополнение к экономике arknet."
				+ "\n она позволяет вам не только получить пассивный источник дохода, но и заработать уникальные возможности"
				+ "\n криптовалюту можно майнить либо купить на бирже , куда ее могут продать майнеры.");
	}

	public void StopAllMining(Main.Mysql m) throws SQLException {
		Main.print("⚠ Криптовалюта умирает и майнеры остальных пользователей выключены");
		int id = 0;
		ResultSet r = m.getField("crypto", "id", "name", '=', this.getName());
		if (r.next())
			id = r.getInt(1);
		m.setField("pc_belong", "status=0", "status", '=', id + "");
	}

	public String getShortData() {
		return ("♻ " + this.name + " - " + this.arc_course + " arckoins");
	}

	static Cryptocurrency getCryptoByName(String name, Main.Mysql m) throws SQLException {
		Cryptocurrency ret = null;
		ArrayList<Cryptocurrency> l = list(m, false);
		for (Cryptocurrency cr : l) {
			if (cr.name.equals(name))
				ret = cr;
		}
		if (ret == null) {
			Main.print("⚠ Tакой криптовалюты не существует");
		}
		return ret;

	}

	static Cryptocurrency create(double comission, long stock, String name, String fromid, Main.Mysql m)
			throws SQLException {
		Cryptocurrency crypt = new Cryptocurrency(comission, stock, name);
		ResultSet rs = m.getField("users", "money", "uid", '=', fromid);
		int pid = 0;
		int cid = 0;
		int money = 0;
		if (rs.next())
			money = rs.getInt(1);
		int owner = Integer.parseInt(fromid);
		if (stock < 100000) {
			System.out.println("⚠ Hачальный фонд должен быть больше 100000");
			return null;
		}
		if (money < stock) {
			System.out.println("⚠ Hедостаточно денег, надо еще " + (stock - money) + " arkcoins");
			return null;
		}

		// проверка на отсутствие крипт с тем же именем и владельцем
		boolean b = false;
		ArrayList<Cryptocurrency> l = Cryptocurrency.list(m, false);
		for (Cryptocurrency cr : l) {
			if (cr.name.equals(name) || cr.owner == owner)
				b = true;
		}

		if (b) {
			System.out.println("⚠ Tакая криптовалюта уже существует либо вы уже обладаете своей криптовалютой");
			return null;
		}
		// добавление новой крипты в базу
		m.addField("crypto", "'" + name + "'", stock + "", crypt.arc_course + "", comission + "", crypt.req + "",
				owner + "", "0", "0", "0", "-1", stock + "", "0");
		// снятие денег
		ResultSet r = m.getField("users", "money", "uid", '=', fromid);
		int cur_money = 0;
		if (r.next()) {
			cur_money = r.getInt(1);
		}
		m.setField("users", "money=" + (cur_money - stock), "uid", '=', fromid);

		// добавление криптовалюты на биржу
		m.addField("crypto_purces", "" + -1 + "", "0");

		r = m.getField("crypto_purces", "id", "type", '=', -1 + "");
		if (r.next()) {
			pid = r.getInt(1);
		}

		r = m.getField("crypto", "id", "name", '=', "'" + name + "'");
		if (r.next()) {
			cid = r.getInt(1);
		}

		m.setField("crypto_purces", "type=" + cid, "id", '=', pid + "");
		m.addField("crypto_belong", "0", pid + "");

		Main.print("✨ Успешное создание");
		return crypt;
	}

	static ArrayList<Cryptocurrency> list(Main.Mysql m, boolean print) throws SQLException {
		ArrayList<Cryptocurrency> res = new ArrayList<Cryptocurrency>();
		try {
			ResultSet rs = m.getAll("crypto");
			while (rs.next()) {
				// String name,int stock, int arc_course, double comission ,double req , int
				// transactions, int summ, long owner, double use , byte flag
				res.add(new Cryptocurrency(rs.getString(2), rs.getLong(3), rs.getInt(4), rs.getDouble(5),
						rs.getDouble(6), rs.getInt(8), rs.getInt(9), rs.getLong(7), rs.getDouble(10), rs.getByte(11)));
			}
		} catch (Exception e) {
			System.out.println("ERROR");
			e.printStackTrace();

		}
		if (res.isEmpty() && print) {
			System.out.print("⚠ На данный момент криптовалют не сущетсвует..");
			return null;
		}

		if (print) {
			for (Cryptocurrency c : res) {
				System.out.println(c.getShortData());
			}

		}
		return res;
	}

	void delete(Main.Mysql m) throws SQLException {
		// берем с базы id криптовалюты
		int cid = 0;
		ResultSet r = m.getField("crypto", "id", "name", '=', "'" + this.name + "'");
		if (r.next()) {
			cid = r.getInt(1);
		}
		// получаем список кошельков с криптовалютой и удаляем иx
		r = m.getField("crypto_purces", "id", "type", '=', "" + cid);
		int pid = 0;
		while (r.next()) {
			pid = r.getInt(1);
			m.deleteField("crypto_purces", "id", '=', "" + pid);
			m.deleteField("crypto_belong", "purse_id", '=', "" + pid);
		}

	}

	static void deleteCrypto(String name, Main.Mysql m) throws SQLException {
		for (Cryptocurrency cr : Cryptocurrency.list(m, false)) {
			if (cr.getName().equals(name))
				cr.delete(m);
		}

	}

	public Cryptocurrency update(Main.Mysql m, double x) throws SQLException {
		this.updatePrice(m, x);
		this.updateReq(m);
		return this;
	}

	private void updatePrice(Main.Mysql m, double x) throws SQLException {
		// 1
		double k = getK(m);
		double a = getA(m);
		double change = k * x * x + a * x + (Math.random() * 0.3 * this.arc_course - 0.15 * this.arc_course)
				+ (Math.random() * 0.25 * this.arc_course - 0.125 * this.arc_course);
		this.arc_course = this.arc_course + (int) (Math.round(change));
		if (this.arc_course <= 0)
			this.arc_course = 1000 + (int) (Math.random() * 4000);
		m.setField("crypto", "course=" + this.arc_course, "name", '=', "'" + this.name + "'");
	}

	private void updateReq(Main.Mysql m) throws SQLException {
		int startfond = 0;
		ResultSet r = m.getField("crypto", "startfond", "name", '=', "'" + this.name + "'");
		if (r.next())
			startfond = r.getInt(1);
		double specpar = (MAX - this.use) / startfond;
		this.req = (1.0d / this.arc_course) + specpar;
		m.setField("crypto", "req=" + this.req, "name", '=', "'" + this.name + "'");
	}

	private double getA(Main.Mysql m) throws SQLException {
		int startfond = 0;

		ResultSet rs = m.getField("crypto", "startfond", "name", '=', "'" + this.name + "'");
		if (rs.next())
			startfond = rs.getInt(1);
		double sub = Crutch.getMantissa(Math.abs(this.stock - startfond));

		return ((sub / this.comission) / 6.25);

	}

	private double getK(Main.Mysql m) throws SQLException {
		double prev_p = 0;
		double p = 0;
		ResultSet rs = m.getField("crypto", "pp", "name", '=', "'" + this.name + "'");
		if (rs.next()) {
			prev_p = rs.getDouble(1);
		}
		if ((prev_p != 0) && ((this.summ + this.transactions) != 0)) {

			p = (double) this.summ / (1000 + this.transactions);
			return (Crutch.getMantissa(prev_p - p) / 6);
		} else {
			p = (double) this.summ / (1000 + this.transactions);
			m.setField("crypto", "pp=" + p, "name", '=', "'" + this.name + "'");
			return 1.5;
		}

	}

}
//HERE

//криптовая биржа, пока будет единственная
class Burse {

	static void updateAll(Main.Mysql m, double t) throws SQLException {
		ArrayList<Cryptocurrency> al = Cryptocurrency.list(m, false);
		for (Cryptocurrency c : al) {
			c.update(m, t);
		}
	}

	static int buy(Main.Mysql my, String name, double eq, String fromid) throws SQLException {
		int money = 0;
		int crypto_id = 0;
		double burse_eq = 0;
		boolean b = false;
		// получение криптовалюты по имени
		Cryptocurrency c = Cryptocurrency.getCryptoByName(name, my);
		// проверка существования криптовалюты
		if (c == null) {
			Main.print("⚠ Криптовалюты с таким именем не сущетсвует");
			return 0;
		}
		if (eq * c.getCourse() < 1) {
			Main.print(
					"⚠ Вы совершаете слишком маленькую покупку, и таким образом \n вы получите криптовалюту не потратив ни одного арккоина");
			return 0;
		}
		// получение кошелька юсера и биржы
		Wallet w = Wallet.getWallet(c, fromid, my);
		Wallet wb = Wallet.getWallet(c, "0", my);
		// получение курса крипты
		int course = c.getCourse();
		// проверка способности крипты к существования
		if (c.getStock() < 0 || c.getStock() == 0) {
			Main.print("⚠ Криптовалюта больше не существует..");
			c.delete(my);
			return 0;
		}

		// получение id крипты
		ResultSet r = my.getField("crypto", "id", "name", '=', "'" + name + "'");
		if (r.next())
			crypto_id = r.getInt(1);
		// получение денег пользователя
		r = my.getField("users", "money", "uid", '=', fromid);
		if (r.next())
			money = r.getInt(1);
		// проверка достаточности денег пользователя
		if (money < eq * c.getCourse()) {
			Main.print("⛔ Недостаточно денег");
			return 0;
		}
		// получение денег биржы
		burse_eq = wb.getMoney();

		// проверка крипты на бирже

		if (burse_eq < eq) {
			Main.print("⛔ На бирже отсутствует такая сумма, сейчас на бирже: " + burse_eq + " " + c.getName());
			return 0;
		} else {
			// если крипта в жизни то комиссия высчитывается с пользователя
			if (c.getFlag() < 1) {
				Main.print("⚠ Запуск транзакции, комиссия: " + (c.getComission() * 100) + " %");// сообщение
																									// пользователям
				double forstock = 0.8 * eq * c.getComission();// деньги которые уходят в фонд
				double forowner = 0.2 * eq * c.getComission();// деньги для владельца крипты
				double comission = forstock + forowner;// общая сумма комиссии
				double foruser = eq - comission;// сумма которая будет начислена пользователю
				int summ = (int) (eq * course);// сумма транзакции

				// снятие денег с юсера
				my.setField("users", "money=" + (money - summ), "uid", '=', fromid);
				// снятие денег с кошелька биржы
				wb.decMoney(foruser, my);
				// начисление денег юсеру на кошелек
				w.addMoney(foruser, my);
				// начисление денег фонду крипты
				my.setField("crypto", "stock=" + Math.round(c.getStock() + forstock * course), "name", '=',
						"'" + c.getName() + "'");
				// начисление процента владельцу
				int owner = 0;
				// получение денег владельца
				r = my.getField("users", "money", "uid", '=', "" + c.getOwner() + "");
				if (r.next()) {
					owner = r.getInt(1);
				}
				// начисление процента владельцу
				my.setField("users", "money=" + Math.round(owner + forowner * course), "uid", '=', c.getOwner() + "");
				my.setField("crypto", "transactions=" + (c.getTransact() + 1), "name", '=', "'" + c.getName() + "'");// увеличение
																														// счетчика
																														// транзакций
				my.setField("crypto", "summ=" + Math.round(c.getSumm() + eq * course), "name", '=',
						"'" + c.getName() + "'");// увеличение суммы транзакций
				// уведомление об успехе
				Main.print(" Совершена транзакция, вам начислено: " + foruser + c.getName() + "`s; " + "комиссия: "
						+ (c.getComission() * 100) + "%");
			} else {
				// если крипта мертвая , то комиссия снимается из фонда пока тот не растратится
				Main.print("⚠ Запуск транзакции, комиссия:" + (c.getComission() * 100) + "%");
				double forstock = 0.8 * eq * c.getComission();// деньги которые уходят из фонда
				double forowner = 0.2 * eq * c.getComission();// деньги для владельца крипты
				double comission = forstock + forowner;// общая сумма комиссии
				double foruser = eq - comission;// сумма которая будет начислена пользователю
				int summ = (int) (eq * course);// сумма транзакции
				my.setField("users", "money=" + Math.round(money - eq * course), "uid", '=', fromid);
				wb.decMoney(eq, my);
				w.addMoney(eq, my);
				int owner = 0;
				r = my.getField("users", "money", "id", '=', "" + c.getOwner() + "");
				if (r.next()) {
					owner = r.getInt(1);
				}
				// высчитывание денег с фонда крипты
				my.setField("crypto", "stock=" + Math.round(c.getStock() - comission * course), "name", '=',
						"'" + c.getName() + "'");
				// начисление денег владельцу
				my.setField("users", "money=" + Math.round(owner + forowner * course), "uid", '=', c.getOwner() + "");
				// увеличение счетчика транзакций
				my.setField("crypto", "transactions=" + (c.getTransact() + 1), "name", '=', "'" + c.getName() + "'");
				// увеличение общей суммы транзакций
				my.setField("crypto", "summ=" + Math.round(c.getSumm() + eq * c.getCourse()), "name", '=',
						"'" + c.getName() + "'");
				Main.print("Совершена транзакция, вам начислено:" + foruser + c.getName() + "`s; " + "комиссия: "
						+ (c.getComission() * 100 * eq * course) + " arkcoin`s");

			}
		}
		return 1;

	}

	static int sell(Main.Mysql my, String name, double eq, String fromid) throws SQLException {
		int money = 0;
		int crypto_id = 0;
		double burse_eq = 0;
		boolean b = false;

		Cryptocurrency c = Cryptocurrency.getCryptoByName(name, my);
		Wallet w = Wallet.getWallet(c, fromid, my);
		Wallet wb = Wallet.getWallet(c, "0", my);
		int course = c.getCourse();
		if ((c.getStock() < 0) || c.getStock() == 0) {
			Main.print("⚠ Криптовалюта больше не существует");
			c.delete(my);
			return 0;
		}
		if (c == null) {
			Main.print("⚠ Криптовалюты с таким именем не существует");
			return 0;
		}
		if ((eq * c.getCourse()) < 1) {
			Main.print("⚠ Слишком маленькая сумма для продажи. \n При такой продаже вы не получете arkcoin`s");
			return 0;
		}
		ResultSet r = my.getField("crypto", "id", "name", '=', "'" + name + "'");
		if (r.next())
			crypto_id = r.getInt(1);

		r = my.getField("users", "money", "uid", '=', fromid);
		if (r.next())
			money = r.getInt(1);

		// получение денег биржы
		burse_eq = wb.getMoney();

		// проверка крипты юсера

		if (w.getMoney() < eq) {
			Main.print("⛔ Недостаточно крипты");
			return 0;
		}
		// транзакция
		else {
			if (c.getFlag() < 1) {
				Main.print("⚠ Запуск транзакции, комиссия:" + (c.getComission() * 100) + "%");
				double forstock = 0.8 * eq * c.getComission();// деньги которые уходят в фонд
				double forowner = 0.2 * eq * c.getComission();// деньги для владельца крипты
				double comission = forstock + forowner;// общая сумма комиссии
				double foruser = eq - comission;// сумма которая будет начислена пользователю
				int summ = (int) (eq * course);// сумма транзакции

				// снятие крипты с юсера
				w.decMoney(eq, my);
				// начисление крипты кошельку биржы
				wb.addMoney(foruser, my);
				// начисление денег юсеру
				my.setField("users", "money=" + Math.round(money + foruser * course), "uid", '=', fromid);
				// начисление денег фонду крипты
				my.setField("crypto", "stock=" + Math.round(c.getStock() + (comission * course)), "name", '=',
						"'" + c.getName() + "'");
				// начисление процента владельцу
				int owner = 0;
				r = my.getField("users", "money", "uid", '=', "" + c.getOwner() + "");
				if (r.next()) {
					owner = r.getInt(1);
				}

				my.setField("users", "money=" + Math.round(owner + forowner * course), "uid", '=', c.getOwner() + "");
				my.setField("crypto", "transactions=" + (c.getTransact() + 1), "name", '=', "'" + c.getName() + "'");
				my.setField("crypto", "summ=" + Math.round(c.getSumm() + summ), "name", '=', "'" + c.getName() + "'");
				Main.print("Совершена транзакция, вам начислено:" + foruser * course + " arkcoin`s; "
						+ "комиссия: " + (c.getComission() * 100 * eq * course) + " arkcoin`s");
			} else {
				Main.print("⚠ Запуск транзакции, комиссия: " + (c.getComission() * 100) + "%");
				double forstock = 0.8 * eq * c.getComission();// деньги которые уходят из фонда
				double forowner = 0.2 * eq * c.getComission();// деньги для владельца крипты
				double comission = forstock + forowner;// общая сумма комиссии
				double foruser = eq - comission;// сумма которая будет начислена пользователю
				// снятие крипты с юсера
				w.decMoney(eq, my);
				// начисление денег кошельку биржы
				wb.addMoney(foruser, my);
				// начисление денег юсеру
				my.setField("users", "money=" + Math.round(money + foruser * course), "uid", '=', fromid);
				// начисление процента владельцу
				int owner = 0;
				r = my.getField("users", "money", "uid", '=', "" + c.getOwner() + "");
				if (r.next()) {
					owner = r.getInt(1);
				}
				// высчитывание денег с фонда крипты
				my.setField("crypto", "stock=" + Math.round(c.getStock() - (comission * course)), "name", '=',
						"'" + c.getName() + "'");

				my.setField("users", "money=" + Math.round(owner + forowner * course), "uid", '=',
						"'" + c.getName() + "'");
				my.setField("crypto", "transactions=" + (c.getTransact() + 1), "name", '=', "'" + c.getName() + "'");
				my.setField("crypto", "summ=" + Math.round(c.getSumm() + eq * c.getCourse()), "name", '=',
						"'" + c.getName() + "'");
				Main.print("Совершена транзакция, вам начислено: " + foruser * course + " arkcoin`s; "
						+ "комиссия: " + (c.getComission() * 100) + " arkcoin`s");
			}
		}
		return 1;
	}
	// перевод крипты другому юсеру

	static int transact(Main.Mysql my, String name, double eq, String fromid, String forid) throws SQLException {
		Cryptocurrency c = Cryptocurrency.getCryptoByName(name, my);
		if (c == null) {
			Main.print("⚠ Криптовалюты с таким именем не сущетсвует");
			return 0;
		}

		Wallet ws = Wallet.getWallet(c, fromid, my);
		Wallet wg = Wallet.getWallet(c, forid, my);
		if (wg.getMoney() < eq) {
			Main.print("⛔ Недостаточно денег");
			return 0;
		} else {
			if (c.getFlag() < 1) {
				double forstock = 0.8 * eq * c.getComission();// деньги которые уходят из фонда
				double forowner = 0.2 * eq * c.getComission();// деньги для владельца крипты
				double comission = forstock + forowner;// общая сумма комиссии
				double for_get_user = eq - comission;// сумма которая будет начислена пользователю

				wg.addMoney(for_get_user, my);
				ws.decMoney(for_get_user, my);
				// начисление денег фонду крипты
				my.setField("crypto", "stock=" + Math.round(c.getStock() + (comission * c.getCourse())), "name", '=',
						c.getName());
				// начисление процента владельцу
				int owner = 0;
				ResultSet r = my.getField("users", "money", "id", '=', "" + c.getOwner());
				if (r.next()) {
					owner = r.getInt(1);
				}
				my.setField("users", "money=" + Math.round(owner + forowner * c.getCourse()), "uid", '=',
						c.getOwner() + "");
				my.setField("crypto", "transactions=" + (c.getTransact() + 1), "name", '=', "'" + c.getName() + "'");
				my.setField("crypto", "summ=" + Math.round(c.getSumm() + eq * c.getCourse()), "name", '=',
						"'" + c.getName() + "'");
				Main.print("⚠ Совершена транзакция, пользователю vk.com/id" + forid + ":" + for_get_user + ""
						+ c.getName() + "`s" + " комиссия:" + c.getComission() + "%");
			} else {
				double forstock = 0.8 * eq * c.getComission();// деньги которые уходят из фонда
				double forowner = 0.2 * eq * c.getComission();// деньги для владельца крипты
				double comission = forstock + forowner;// общая сумма комиссии
				double for_get_user = eq - comission;// сумма которая будет начислена пользователю

				if (c.getStock() <= 0) {
					Main.print("⚠ криптовалюта больше не существует");
					c.delete(my);
					return 0;
				}

				wg.addMoney(for_get_user, my);
				ws.decMoney(for_get_user, my);
				// cнятие денег фонду крипты
				my.setField("crypto", "stock=" + Math.round(c.getStock() - (comission * c.getCourse())), "name", '=',
						"'" + c.getName() + "'");
				// начисление процента владельцу
				int owner = 0;
				ResultSet r = my.getField("users", "money", "uid", '=', "" + c.getOwner());
				if (r.next()) {
					owner = r.getInt(1);
				}
				my.setField("users", "money=" + Math.round(owner + forowner * c.getCourse()), "uid", '=',
						c.getOwner() + "");
				my.setField("crypto", "transactions=" + (c.getTransact() + 1), "name", '=', "'" + c.getName() + "'");
				my.setField("crypto", "summ=" + Math.round(c.getSumm() + eq * c.getCourse()), "name", '=',
						"'" + c.getName() + "'");
				Main.print("⚠ Совершена транзакция, пользователю vk.com/id" + forid + ":" + for_get_user + ""
						+ c.getName() + "`s" + " комиссия:" + c.getComission() + "%");
			}
		}
		return 1;
	}
}

class Wallet {
	Cryptocurrency type;
	double money;
	long id;

	private Wallet(Cryptocurrency type, double money, long id) {
		this.type = type;
		this.money = money;
		this.id = id;
	}

	public double getMoney() {
		return money;
	}

	public long getId() {
		return id;
	}

	public Cryptocurrency getType() {
		return type;
	}

	public String toString() {
		return "номер кошелька:" + this.id + ";криптовалюта:" + type.getName() + ";счет:" + this.money;
	}

	static Wallet getWallet(Cryptocurrency type, String fromid, Main.Mysql m) throws SQLException {
		int cid = 0;
		int pid = cid;
		double money = 0;
		// проверка существования кошелька
		if (type == null) {
			Main.print("⚠ Криптовалюты не существует");
			return null;
		}
		if (checkWalletExistance(type, fromid, m)) {
			// получение id криптовалюты
			ResultSet r = m.getField("crypto", "id", "name", '=', "'" + type.getName() + "'");
			if (r.next()) {
				cid = r.getInt(1);
			}
			// получение списка кошельков юсера
			ResultSet rs = m.getField("crypto_belong", "purse_id", "uid", '=', fromid);
			ResultSet r_2 = null;
			int type_purse = 0;
			int id_purse = 0;
			// нахождение кошелька от нужной крипты
			while (rs.next()) {
				id_purse = rs.getInt(1);
				r_2 = m.getField("crypto_purces", "type", "id", '=', id_purse + "");
				if (r_2.next()) {
					type_purse = r_2.getInt(1);
					if (type_purse == cid) {
						pid = id_purse;
					}
				}
			}

			// получение денег на кошельке
			r_2 = m.getField("crypto_purces", "money", "id", '=', pid + "");
			if (r_2.next()) {
				money = r_2.getDouble(1);
			}
			// возвращение обьекта кощелька
			return new Wallet(type, money, pid);
		}

		else {
			Main.print("⚠ Кошелек не создан, создание...");
			return createWallet(type, fromid, m);
		}

	}

	static boolean checkWalletExistance(Cryptocurrency type, String fromid, Main.Mysql m) throws SQLException {
		int id = 0;
		// проверка существования криптовалюты

		boolean b = false;
		// получение id криптовалюты
		ResultSet r = m.getField("crypto", "id", "name", '=', "'" + type.getName() + "'");
		if (r.next()) {
			id = r.getInt(1);
		}
		ResultSet r_2 = null;
		ResultSet rs = m.getField("crypto_belong", "purse_id", "uid", '=', fromid);
		while (rs.next()) {
			int purseid = rs.getInt(1);

			r_2 = m.getField("crypto_purces", "type", "id", '=', purseid + "");
			if (r_2.next()) {
				int tid = r_2.getInt(1);

				b = (tid == id);

				if (b)
					return true;
			}
		}
		return false;
	}

	private static Wallet createWallet(Cryptocurrency type, String fromid, Main.Mysql m) throws SQLException {
		int id = 0;
		Wallet p = null;
		if (checkWalletExistance(type, fromid, m)) {
			throw new IllegalStateException("[IO] Кошелек существует");
		}

		int pid = 0;
		// проверка существования криптовалюты
		if (type == null)
			return null;
		ResultSet r = m.getField("crypto", "id", "name", '=', "'" + type.getName() + "'");
		if (r.next()) {
			id = r.getInt(1);
		}
		// костыль для добавления кошелька в бд
		m.addField("crypto_purces", "-1", "0");

		r = m.getField("crypto_purces", "id", "type", '=', "-1");
		if (r.next()) {
			pid = r.getInt(1);
		}
		m.addField("crypto_belong", fromid, "" + pid);
		p = new Wallet(type, 0, pid);
		m.setField("crypto_purces", "type=" + id + "", "id", '=', pid + "");
		return p;
	}

	boolean addMoney(double eq, Main.Mysql m) throws SQLException {
		double cur_money = 0;
		ResultSet r = m.getField("crypto_purces", "money", "id", '=', this.id + "");
		if (r.next()) {
			cur_money = r.getDouble(1);
		}
		m.setField("crypto_purces", "money=" + (cur_money + eq), "id", '=', this.id + "");
		return true;
	}

	boolean decMoney(double eq, Main.Mysql m) throws SQLException {
		double cur_money = 0;
		ResultSet r = m.getField("crypto_purces", "money", "id", '=', this.id + "");
		if (r.next()) {
			cur_money = r.getDouble(1);
		}
		if (cur_money < eq) {
			Main.print("⛔ Kритическая ошибка");
			throw new IllegalStateException("[IO] Недостаточно денег");
		}
		m.setField("crypto_purces", "money=" + (cur_money - eq), "id", '=', this.id + "");
		return true;
	}

	//
	boolean deleteWallet(Main.Mysql m) throws SQLException {
		m.deleteField("crypto_purces", "id", '=', this.id + "");
		m.deleteField("crypto_belong", "purse_id", '=', this.id + "");
		return true;
	}

}

//класс для управления ПК пользователей
class PCManager {
	public final static byte EMPTY = 0;
	public final static byte BROKEN = -1;
	public final static byte NEW = -2;

	public static void PCInfo() {
		Main.print("💻 Компьютеры: каждый пользователь может собрать свой компьютер, из разных деталей \n"
				+ "чем лучше детали, тем реже компьютер выходит из строя и майнит больше криптовалюты \n"
				+ "количество компьютеров, которое может находится у пользователя зависит от количества слотов в доме");
	}

//String getDetailInfo(int id, String type, boolean full, Main.Mysql m) 
	public static OutputManager getDetailListForType(int type, int firstMessageDec, int use_symbols, Main.Mysql m)
			throws SQLException {
		String stype = PC.getTextId(type);
		ResultSet result = m.getField("eshop", "id", "type", '=', "'" + stype + "'");
		OutputManager ret = new OutputManager();
		int i = 0;
		int j = 1;
		String s = "";
		while (result.next()) {
			i++;
			if (j == 1) {
				if ((s + "" + i + ")" + getDetailInfo(result.getInt(1), PC.getTextId(type), true, m))
						.length() > firstMessageDec) {
					j++;
					ret.addPart(s);
					s = "" + i + ")" + getDetailInfo(result.getInt(1), PC.getTextId(type), true, m);
				} else {
					s += "" + i + ")" + getDetailInfo(result.getInt(1), PC.getTextId(type), true, m);
				}
			} else {
				if ((s + "" + i + ")" + getDetailInfo(result.getInt(1), PC.getTextId(type), true, m))
						.length() > use_symbols) {
					j++;
					ret.addPart(s);
					s = "" + i + ")" + getDetailInfo(result.getInt(1), PC.getTextId(type), true, m);
				} else {
					s += "" + i + ")" + getDetailInfo(result.getInt(1), PC.getTextId(type), true, m);
				}
			}
		}
		ret.addPart(s);
		return ret;
	}

	public static int getDetailTypeId(int id, Main.Mysql m) throws SQLException {
		ResultSet result = m.getField("eshop", "type", "id", '=', "" + id);

		if (result.next()) {

			switch (result.getString(1)) {
			case "hull":
				return 1;

			case "video":
				return 2;

			case "motherboard":
				return 3;

			case "ram":
				return 4;

			case "processor":
				return 5;

			case "memory":
				return 6;

			case "chargecontroller":
				return 7;

			case "output":
				return 8;

			case "cooler":
				return 9;

			case "inet":
				return 10;

			}
		} else {
			Main.print("⛔ Деталь не найдена");
			return 0;
		}

		return 0;
	}

	public static void printPCInformation(long id, String uid, Main.Mysql m) throws SQLException {
		ResultSet result = m.getField("pc_belong", "uid", "pc_id", '=', id + "");
		String s = "";

		if (result.next()) {
			s = result.getString(1);
		}
		if (!s.equals(uid)) {
			Main.print("⛔ Нет доступа");
			Main.print("Краткая информация, владелец пк vk.com/id"+s + " \n");
			PC pc = getPC(m,id);
			Main.print(pc.getShortPCInfo(m));
			return;
		}

		result = m.getField("pc", "*", "id", '=', id + "");
		ResultSetMetaData meta = result.getMetaData();

		if (result.next()) {
			Main.print("========================\nИнформация pc:" + result.getInt(1) + "\n========================\n"
					+ "слот 1)" + getDetailInfo(result.getInt(2), meta.getColumnName(2), false, m) + "\n" + "слот 2)"
					+ getDetailInfo(result.getInt(3), meta.getColumnName(3), false, m) + "\n" + "слот 3)"
					+ getDetailInfo(result.getInt(4), meta.getColumnName(4), false, m) + "\n" + "слот 4)"
					+ getDetailInfo(result.getInt(5), meta.getColumnName(5), false, m) + "\n" + "слот 5)"
					+ getDetailInfo(result.getInt(6), meta.getColumnName(6), false, m) + "\n" + "слот 6)"
					+ getDetailInfo(result.getInt(7), meta.getColumnName(7), false, m) + "\n" + "слот 7)"
					+ getDetailInfo(result.getInt(8), meta.getColumnName(8), false, m) + "\n" + "слот 8)"
					+ getDetailInfo(result.getInt(9), meta.getColumnName(9), false, m) + "\n" + "слот 9)"
					+ getDetailInfo(result.getInt(10), meta.getColumnName(10), false, m) + "\n" + "слот 10)"
					+ getDetailInfo(result.getInt(11), meta.getColumnName(11), false, m) + "\n");
		} else {
			Main.print("⚠ Компьютер не существует");
		}

	}

	public static ArrayList<PC> getAllUserPC(String fromid, Main.Mysql m) throws SQLException {
		ArrayList<PC> pclist = new ArrayList<PC>();
		ResultSet r = m.getField("pc_belong", "pc_id", "uid", '=', fromid);
		int id = 0;
		while (r.next()) {
			pclist.add(PC.getPC(m, r.getInt(1)));
		}
		return pclist;
	}

	public static int getPCCount(Main.Mysql m, long uid) throws SQLException {
		ResultSet r = m.getField("pc_belong", "*", "uid", '=', "" + uid);
		int i = 0;
		while (r.next()) {
			i++;
		}
		return i;
	}

	public static int getUserSlots(Main.Mysql m, long uid) throws SQLException {
		int slots = getPCCount(m, uid);
		String home = "";
		ResultSet r = m.getField("users", "home", "uid", '=', "" + uid);
		if (r.next()) {
			home = r.getString(1);
		}
		slots = getHomeSlots(home) - slots;
		return slots;
	}

	public static int getHomeSlots(String home) {
		switch (home) {
		case "ussrflat":
		case "apartamentq":
			return 1;
		case "apartmentw":
			return 3;
		case "apartmentc":
		case "center":
			return 5;
		case "smallzagorod":
			return 7;
		case "bigzagorod":
			return 10;
		case "cottage":
			return 12;
		case "eliteappartament":
			return 6;
		case "mansion":
			return 20;
		case "villa":
			return 30;
		case "homeingeneva":
			return 10;
		case "palaceinturkey":
			return 45;
		default:
			return 0;
		}
	}

	public static boolean buyDetail(int uid, int did, int type, PC pc, Main.Mysql my) throws SQLException {
		ResultSet r = my.getField("pc_belong", "uid", "pc_id", '=', "" + pc.getId());
		String s = "";
		if (r.next()) {
			s = r.getString(1);
		}
		if (pc == null) {
			Main.print("⛔ Ошибка поиска компьютера");
			return false;
		}
		r = my.getField("users", "money", "uid", '=', "" + uid);
		int money = 0;
		int det_price = 0;
		if (s.equals(uid + "")) {
			if (r.next())
				money = r.getInt(1);
			r = my.getField("eshop", "price", "id", '=', did + "");
			if (r.next()) {
				det_price = r.getInt(1);
			} else {
				Main.print("⛔ Детали не существует");
				return false;
			}
			if (money < det_price) {
				Main.print("⛔ Недостаточно денег");
				return false;
			} else {
				if (pc.hasDetail(type, my)) {
					pc.changeDetail(type, did, uid + "", my);
					my.setField("users", "money=" + (money - det_price), "uid", '=', uid + "");
				} else {
					pc.setDetail(type, did, uid + "", my);
					my.setField("users", "money=" + (money - det_price), "uid", '=', uid + "");
				}

			}
		} else {
			Main.print("⛔ Этот компьютер принадлежит другому пользователю");
			return false;
		}
		r = my.getField("eshop", "name", "id", '=', "" + did);
		String name = "";
		if (r.next()) {
			name = r.getString(1);
		}
		Main.print("💳 Вы купили " + getTypeNameR(PC.getTextId(type)) + " : " + name + " в компьютер №" + pc.getId());
		return true;
	}

	public static boolean sellDetail(int uid, int type, PC pc, Main.Mysql my) throws SQLException {
		ResultSet r = my.getField("pc_belong", "uid", "pc_id", '=', "" + pc.getId());
		int did = 0;
		String s = "";
		if (r.next()) {
			s = r.getString(1);
		}
		r = my.getField("pc", PC.getTextId(type), "id", '=', "" + pc.getId());
		if (r.next())
			did = r.getInt(1);
		if (s.equals(uid + "")) {
			if (pc.hasDetail(type, my)) {
				int money = 0;
				int det_price = 0;
				r = my.getField("users", "money", "uid", '=', "" + uid);
				if (r.next())
					money = r.getInt(1);

				r = my.getField("eshop", "price", "id", '=', did + "");
				if (r.next())
					det_price = r.getInt(1);
				int addmoney = (int) det_price / 2;
				money = money + addmoney;
				my.setField("users", "money=" + money, "uid", '=', "" + uid);
				pc.removeDetail(type, uid + "", my);

			} else {
				Main.print("⚠ В этом компьютере нет такой детали");
				return false;
			}
		} else {
			Main.print("⛔ Этот компьютер принадлежит другому пользователю");
			return false;
		}
		Main.print("💰 Вы продали " + getTypeNameR(PC.getTextId(type)) + " из своего компьютера №" + pc.getId());
		return true;
	}

	public static String getTypeNameR(String s) {
		switch (s) {
		case "hull":
			return "Корпус: ";

		case "video":
			return "Видеокарта: ";

		case "motherboard":
			return "Материнская плата: ";

		case "ram":
			return "Оперативная память: ";

		case "processor":
			return "Процессор: ";

		case "memory":
			return "Винчестер: ";

		case "chargecontroller":
			return "Бесперебойник: ";

		case "output":
			return "Устройство вывода: ";

		case "cooler":
			return "Кулер: ";

		case "inet":
			return "Интернет-модуль: ";
		default:
			return "не обнаружено названия";
		}
	}

	public static String getDetailInfo(int id, String type, boolean full, Main.Mysql m) throws SQLException {
		String ret = "";
		ResultSet result = m.getField("eshop", "name,price,power,energyusing, dangerous", "id", '=', id + "");
		if (!full) {
			ret += getTypeNameR(type);
		}
		if (id == 0) {
			ret += "Отсутствует";
			return ret;
		} else if (result.next()) {
			ret += "" + result.getString(1);
			if (!full)
				return ret;
			else {
				ret += ";цена:" + result.getInt(2) + " arkcoins";
				ret += ";мощность:" + result.getInt(3) + " sol/h";
				ret += ";энергопотребление:" + result.getInt(4) + "квт/ч;";
				int dangerous = result.getInt(5);
				ret += "вероятность поломки : " + dangerous + "%";
				ret += ";id детали(для покупки):" + id + "\n";
			}

		} else {
			Main.print("⛔ Не найдена деталь");
			return "";
		}
		return ret;
	}

	// public void brokeAll(Main.Mysql m){}
	static class PC {

		private int id;
		private int status;
		private int power;
		private int dangerous;
		private int energyusing;

		private PC() {
			// конструктор-заглушка
		}

		private PC(int id, Main.Mysql m) {
			this(id, 0, 0, 0, 0, m);
		}

		private PC(int id, int status, int power, int dangerous, int energyusing, Main.Mysql m) {
			this.id = id;
			this.status = status;
			this.power = power;
			this.dangerous = dangerous;
			this.energyusing = energyusing;
		}

		public static boolean checkWorkable(int id, Main.Mysql m) throws SQLException {
			ResultSet r = m.getField("crypto_belong", "status", "pc_id", '=', id + "");
			int status = 0;
			if (r.next()) {
				status = r.getInt(1);
			}

			if (status >= 0) {
				return true;
			}
			return false;
		}

		public void changeStatus(int action, String uid, Main.Mysql m) throws SQLException {
			// 0 поломать
			// 1 починить
			// 2 проверить готовность к работе , после установки детали
			switch (action) {
			case 0:
				// MiningManager.getHideProfit(owner,this.id,System.currentTimeMillis(),m);
				this.broke(m);
				break;
			case 1:
				this.repair(uid, m);
				break;
			case 2:
				ResultSet r = m.getField("pc_belong", "status", "pc_id", '=', "" + this.id);
				int previous_status = 0;
				if (r.next()) {
					previous_status = r.getInt(1);
				}
				if (previous_status > 0) {
					ResultSet r_2 = m.getField("pc_belong", "uid", "pc_id", '=', "" + this.id);
					String owner = "";
					if (r_2.next()) {
						owner = r_2.getString(1);
					}
					MiningManager.getHideProfit(owner, this.id, System.currentTimeMillis(), m);
				}
				r = m.getField("pc", "*", "id", '=', "" + this.id);
				boolean b = true;
				if (r.next()) {
					for (int i = 2; i < 12; i++) {
						b = b & !(r.getInt(i) == 0);
					}
				}
				if (b) {
					m.setField("pc_belong", "status=" + EMPTY, "pc_id", '=', "" + this.id);
				} else {
					m.setField("pc_belong", "status=" + NEW, "pc_id", '=', "" + this.id);
				}
				break;

			default:
				throw new IllegalStateException("произошла ошибка");
			}
		}

		private void broke(Main.Mysql m) throws SQLException {
			this.status = BROKEN;
			m.setField("pc_belong", "status=" + this.status, "pc_id", '=', "" + this.id);
			ResultSet r = m.getField("pc", "*", "id", '=', "" + this.id);
			int det = 0;
			if (r.next()) {
				det = r.getInt((int) (Math.random() * 11 + 2));
			}
			int repair = 0;
			r = m.getField("eshop", "price", "id", '=', "" + det);
			if (r.next()) {
				repair = r.getInt(1) / 2;
			}
			m.setField("pc_belong", "repair=" + repair, "pc_id", '=', "" + this.id);
		}

		// sellDetail(int uid, int did, int type, PC pc, Main.Mysql my)
		void destroy(Main.Mysql m, String uid) throws SQLException {
			int owner = 0;
			ResultSet result = m.getField("pc", "*", "id", '=', "" + this.id);
			ResultSet addr = null;
			addr = m.getField("pc_belong", "uid", "pc_id", '=', "" + this.id);
			String s = "";

			if (result.next()) {
				if (addr.next()) {
					s = addr.getString(1);
				}
				if (s.equals(uid)) {
					for (int i = 2; i < 12; i++) {
						addr = m.getField("pc", getTextId(i - 1), "id", '=', "" + this.id);
						if (addr.next()) {
							sellDetail(Integer.parseInt(uid), i - 1, this, m);
						}
					}

					m.deleteField("pc", "id", '=', "" + this.id);
					m.deleteField("pc_belong", "pc_id", '=', "" + this.id);
					Main.print("💥 💥 Компьютер успешно уничтожен 💥 💥");
					return;
				} else {
					Main.print("⛔ Этот компьютер принадлежит другому человеку");
					return;
				}
			} else {
				Main.print("🔎 Компьютер не найден");

			}
		}

		void repair(String uid, Main.Mysql m) throws SQLException {
			ResultSet r = m.getField("pc_belong", "uid", "pc_id", '=', "" + this.id);
			String s = "";
			int rep = 0;
			if (r.next()) {
				s = r.getString(1);
			}
			r = m.getField("pc_belong", "repair", "pc_id", '=', "" + this.id);
			if (r.next()) {
				rep = r.getInt(1);
			}

			if (s.equals(uid + "")) {
				r = m.getField("users", "money", "uid", '=', uid);
				if (r.next()) {
					int usermoney = r.getInt(1);
					if (usermoney < rep) {
						Main.print("⛔ Недостаточно денег на ремонт, необходимо:" + " " + rep);
						return;
					} else {
						m.setField("pc_belong", "status=" + EMPTY, "pc_id", '=', "" + this.id);
						m.setField("users", "money=" + (usermoney - rep), "id", '=', uid);
						Main.print("😊 Компьютер готов к работе");
					}
				}
			} else {
				Main.print("⛔ Этот компьютер принадлежит другому пользователю");
				return;
			}
		}

		static PC createPC(Main.Mysql m, String uid) throws SQLException {
			PC mypc = null;
			if (getUserSlots(m, Long.parseLong(uid)) > 0) {
				m.addField("pc", "-1", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0");
				ResultSet rs = m.getField("pc", "id", "hull", '=', "-1");
				if (rs.next()) {
					mypc = new PC(rs.getInt(1), m);
				}
				m.setField("pc", "hull=0", "id", '=', "" + mypc.id);

				m.addField("pc_belong", uid, mypc.id + "", NEW + "", "0", "0", "0");

				return mypc;

			} else {
				if (getUserSlots(m, Long.parseLong(uid)) == 0) {
					Main.print("⚠ Все слоты заняты");
				} else {
					throw new IllegalStateException("произошла ошибка");
				}
			}
			return mypc;
		}

		public static PC getPC(Main.Mysql m, long id) throws SQLException {
			PC mypc = null;

			ResultSet result = m.getField("pc", "*", "id", '=', id + "");
			int power = 0;
			int dangerous = 0;
			int status = 0;
			int energyusing = 0;
			if (result.next()) {
				power = result.getInt(12);
				dangerous = result.getInt(14);
				energyusing = result.getInt(13);
			} else {
				Main.print("🔎 Компьютер не найден");
				return null;
			}
			result = m.getField("pc_belong", "status", "pc_id", '=', id + "");
			if (result.next()) {
				status = result.getInt(1);
			}
			// Main.print("here");
			mypc = new PC(id, status, power, dangerous, energyusing, m);
			return mypc;
		}

		public int getStatus() {
			return this.status;
		}

		public int getId() {
			return id;
		}

		public int getPower() {
			return power;
		}

		public int getDangerous() {
			return dangerous;
		}

		public int getEnergyusing() {
			return energyusing;
		}

		public String getShortPCInfo(Main.Mysql m) throws SQLException {
			String status = "";
			switch (this.status) {
			case EMPTY:
				status += "\n 🔎 Не майнит; \n";
				break;
			case NEW:
				status += "\n 📼 Не готов к работе; \n";
				break;
			case BROKEN:
				status += "\n ⛔ Cломан \n";
				break;
			default: {
				ResultSet resultSet = m.getField("crypto", "name", "id", '=', this.status + "");
				if (resultSet.next()) {
					status += "\n 📠 Майнит " + resultSet.getString(1) + "\n";
				}
			}
			}
			String s = "";
			if (this.dangerous < 0)
				s = "\n 🌈 Абсолютно безопасный\n";
			else
				s = "\n 🆘 Опасность:" + this.dangerous + " %\n";
			if (this.dangerous > 100)
				s = "\n 🔥 Предельно опасный \n";
			return "💻 Kомпьютер № " + this.id + status + "📡 Мощность:" + this.power + " sol/h\n " + " 💡 Энергопотребление: "
					+ this.energyusing + " квт/ч" + s;
		}

		void setDetail(int type, int id, String uid, Main.Mysql m) throws SQLException {
			String stype = getTextId(type);
			ResultSet r = m.getField("eshop", "*", "id", '=', id + "");
			if (r.next()) {
				m.setField("pc", stype + "=" + id, "id", '=', this.id + "");

				this.power += r.getInt(5);
				this.energyusing += r.getInt(6);
				this.dangerous += r.getInt(7);
				m.setField("pc",
						"power=" + this.power + " , energyusing=" + this.energyusing + " , dangerous=" + this.dangerous,
						"id", '=', "" + this.id);
				this.changeStatus(2, uid, m);
			} else {
				Main.print("⚠ Деталь не найдена");
				return;
			}
		}

		void removeDetail(int type, String uid, Main.Mysql m) throws SQLException {
			String t = getTextId(type);
			ResultSet r = m.getField("pc", "" + getTextId(type), "id", '=', this.id + "");
			ResultSet rin = null;
			if (r.next()) {
				rin = m.getField("eshop", "*", "id", '=', r.getInt(1) + "");
				if (rin.next()) {
					m.setField("pc", t + "=" + "0", "id", '=', this.id + "");
					this.power -= rin.getInt(5);
					this.energyusing -= rin.getInt(6);
					this.dangerous -= rin.getInt(7);
					m.setField("pc", "power=" + this.power + " , energyusing=" + this.energyusing + " , dangerous="
							+ this.dangerous, "id", '=', "" + this.id);
				}
				this.changeStatus(2, uid, m);
			} else {
				Main.print("⚠ Такой детали нет");
				return;
			}
		}

		public boolean hasDetail(int type, Main.Mysql m) throws SQLException {
			String s = getTextId(type);
			ResultSet rs = m.getField("pc", s, "id", '=', this.id + "");
			if (rs.next()) {
				if (rs.getInt(1) == 0) {
					return false;
				} else
					return true;

			}
			return true;
		}

		void changeDetail(int type, int newd, String uid, Main.Mysql m) throws SQLException {
			this.removeDetail(type, uid, m);
			this.setDetail(type, newd, uid, m);
		}

		public static String getTextId(int type) {
			switch (type) {
			case 1:
				return "hull";

			case 2:
				return "video";

			case 3:
				return "motherboard";

			case 4:
				return "ram";

			case 5:
				return "processor";

			case 6:
				return "memory";

			case 7:
				return "chargecontroller";

			case 8:
				return "output";

			case 9:
				return "cooler";

			case 10:
				return "inet";

			default:
				throw new IllegalArgumentException("Неверный тип детали");
			}
		}
	}

}

//управление майнингом
class MiningManager {
	public static void MiningInfo() {
		Main.print(
				"📡 Майнинг: чтобы начать майнить криптовалюту вам необходимо иметь работоспособный компьютер,который \n"
						+ "обладает достаточный мощностью для майнинга, минимальная мощность указана в характеристиках криптовалюты, и \n"
						+ "она меняется с количеством добытых монет и в зависимости от курса криптовалюты.После того, как вы выставили компьютер \n"
						+ "в поток, то он начинает майнить.Вы можете в любое время отключить майнинг и собрать деньги, которые будут начислены на ваш \n"
						+ "кошелек");

	}

	public MiningManager() {
		// конструктор-заглушка
	}

	public static void setMiningPool(String uid, int pc_id, String crypto, long time, Main.Mysql m)
			throws SQLException {
		int cid = 0;
		int power = 0;
		PCManager.PC pc = PCManager.PC.getPC(m, pc_id);
		Cryptocurrency c = Cryptocurrency.getCryptoByName(crypto, m);
		if (c == null)
			return;
		ResultSet r = m.getField("pc_belong", "uid", "pc_id", '=', "" + pc.getId());
		String s = "";
		if (r.next()) {
			s = r.getString(1);
		}
		byte flag = 0;
		r = m.getField("crypto", "flag", "name", '=', "'" + c.getName() + "'");
		if (r.next()) {
			flag = r.getByte(1);
		}
		if (flag > 0) {
			Main.print("⚠ Криптовалюта закрыта для майнинга");
			return;
		}
		if (s.equals(uid + "")) {

			r = m.getField("crypto", "id", "name", '=', "'" + crypto + "'");
			if (r.next()) {
				cid = r.getInt(1);
			}
			Cryptocurrency formining = Cryptocurrency.getCryptoByName(crypto, m);
			r = m.getField("pc", "power", "id", '=', pc.getId() + "");
			if (r.next()) {
				power = r.getInt(1);
			}
			if (power < (int) (1 / formining.getReq())) {
				Main.print("⛔ У этого компьютера недостаточно мощности для майнинга этой криптовалюты");
			}
			if (pc.getStatus() < 0) {
				Main.print("⛔ Компьютер не доступен для майнинга");
				return;
			} else {
				m.setField("pc_belong", "status=" + cid, "pc_id", '=', "" + pc.getId());
				m.setField("pc_belong", "mining_time=" + time, "pc_id", '=', pc.getId() + "");
				Main.print("🔎 Компьютер запущен в поток майнинга");
			}
		} else {
			Main.print("⛔ Этот компьютер принадлежит другому пользователю");
			return;
		}
	}

	public static void leaveMiningPool(String uid, int pc_id, long time, Main.Mysql m) throws SQLException {
		ResultSet r = m.getField("pc_belong", "uid", "pc_id", '=', "" + pc_id);
		String s = "";
		if (r.next()) {
			s = r.getString(1);
			if (s.equals(uid + "")) {
				m.setField("pc_belong", "status=0", "pc_id", '=', pc_id + "");
			} else {
				Main.print("⛔ Этот компьютер принадлежит другому пользователю");
			}
		}

	}

	public static void getProfit(String uid, int pc_id, long time, Main.Mysql m) throws SQLException {
		ResultSet rs = null;
		int cid = 0;
		rs = m.getField("pc_belong", "status", "pc_id", '=', "" + pc_id);
		if (rs.next()) {
			cid = rs.getInt(1);
		}
		if (cid <= 0) {
			Main.print("⛔ Компьютер не майнит");
			return;
		}
		Cryptocurrency c = null;
		rs = m.getField("crypto", "name", "id", '=', "" + cid);
		if (rs.next()) {
			c = Cryptocurrency.getCryptoByName(rs.getString(1), m);
		}
		PCManager.PC pc = PCManager.PC.getPC(m, pc_id);
		rs = m.getField("pc_belong", "uid", "pc_id", '=', "" + pc_id);
		String s = "";
		if (rs.next()) {
			s = rs.getString(1);
		}
		if (s.equals(uid + "")) {
			long prev_time = 0;
			rs = m.getField("pc_belong", "mining_time", "pc_id", '=', "" + pc_id);
			if (rs.next()) {
				prev_time = rs.getLong(1);
			}
			double profit = ((double) (time - prev_time) / (3600 * 100000)) * c.getReq();
			double inuse = 0.0;
			rs = m.getField("crypto", "inuse", "name", '=', "'" + c.getName() + "'");
			if (rs.next()) {
				inuse = rs.getDouble(1);
			}
			Wallet w = Wallet.getWallet(c, uid, m);
			if (profit > (Cryptocurrency.MAX - inuse)) {
				profit = (Cryptocurrency.MAX - inuse);
				m.setField("crypto", "flag=1", "name", '=', "'" + c.getName() + "'");
				Main.print("⚠ Вы добыли последние возможные криптомонеты, теперь майнинг больше не доступен!");
				m.setField("crypto_purces", "money=" + (w.getMoney() + profit), "id", '=', w.getId() + "");
				m.setField("crypto", "inuse=" + (c.getUse() + profit), "name", '=', "'" + c.getName() + "'");
				m.setField("pc_belong", "mining_time=" + time, "pc_id", '=', "" + pc_id);
				Main.print("💰 Вы добыли: " + OutputManager.getFractionPrefix(profit) + " " + c.getName() + "`s");
			} else {
				m.setField("crypto_purces", "money=" + (w.getMoney() + profit), "id", '=', w.getId() + "");
				m.setField("crypto", "inuse=" + (c.getUse() + profit), "name", '=', "'" + c.getName() + "'");
				m.setField("pc_belong", "mining_time=" + time, "pc_id", '=', "" + pc_id);
				Main.print("💰 Вы добыли: " + OutputManager.getFractionPrefix(profit) + " " + c.getName() + "`s");
			}
		} else {
			Main.print("⛔ Нельзя собирать деньги с чужих компьютеров");
		}
	}

	public static void getHideProfit(String uid, int pc_id, long time, Main.Mysql m) throws SQLException {
		ResultSet rs = null;
		int cid = 0;
		rs = m.getField("pc_belong", "status", "pc_id", '=', "" + pc_id);
		if (rs.next()) {
			cid = rs.getInt(1);
		}
		if (cid <= 0) {
			Main.print("⛔ Компьютер не майнит");
			return;
		}
		Cryptocurrency c = null;
		rs = m.getField("crypto", "name", "id", '=', "" + cid);
		if (rs.next()) {
			c = Cryptocurrency.getCryptoByName(rs.getString(1), m);
		}
		PCManager.PC pc = PCManager.PC.getPC(m, pc_id);
		rs = m.getField("pc_belong", "uid", "pc_id", '=', "" + pc_id);
		String s = "";
		if (rs.next()) {
			s = rs.getString(1);
		}
		if (s.equals(uid + "")) {
			long prev_time = 0;
			rs = m.getField("pc_belong", "mining_time", "pc_id", '=', "" + pc_id);
			if (rs.next()) {
				prev_time = rs.getLong(1);
			}
			double profit = ((double) (time - prev_time) / (3600 * 100000)) * c.getReq();
			double inuse = 0.0;
			rs = m.getField("crypto", "inuse", "name", '=', "'" + c.getName() + "'");
			if (rs.next()) {
				inuse = rs.getDouble(1);
			}
			Wallet w = Wallet.getWallet(c, uid, m);
			if (profit > (Cryptocurrency.MAX - inuse)) {
				profit = (Cryptocurrency.MAX - inuse);
				m.setField("crypto", "flag=1", "name", '=', "'" + c.getName() + "'");
				Main.print("⚠ Добыты последние возможные криптомонеты, теперь майнинг больше не доступен!");
				m.setField("crypto_purces", "money=" + (w.getMoney() + profit), "id", '=', w.getId() + "");
				m.setField("crypto", "inuse=" + (c.getUse() + profit), "name", '=', "'" + c.getName() + "'");
				m.setField("pc_belong", "mining_time=" + time, "pc_id", '=', "" + pc_id);
			} else {
				m.setField("crypto_purces", "money=" + (w.getMoney() + profit), "id", '=', w.getId() + "");
				m.setField("crypto", "inuse=" + (c.getUse() + profit), "name", '=', "'" + c.getName() + "'");
				m.setField("pc_belong", "mining_time=" + time, "pc_id", '=', "" + pc_id);
			}
		} else {
			Main.print("⛔ Нельзя собирать деньги с чужих компьютеров");
		}
	}

}