import java.lang.*;
import java.sql.*;
import java.io.*;
import java.util.Scanner;
import java.util.*;

class Main {

	final private static Mysql m = Mysql.get();

	public final static int MESSAGE_SIZE = 900;

	static class Mysql {

		private Mysql() {

		}

		//
		static Mysql get() {
			return new Mysql();
		}

		ResultSet getAll(String table) throws SQLException {
			Statement s = this.connect();
			String query = "SELECT * " + "FROM " + table;
			return s.executeQuery(query);
		}

		ResultSet getField(String table, String field, String search, char cond_symbol, String searchvalue)
				throws SQLException {
			Statement s = this.connect();
			String query = "SELECT " + field + " FROM " + table + " WHERE " + search + "" + cond_symbol + ""
					+ searchvalue;
			// print(query);
			return s.executeQuery(query);
		}

		int setField(String table, String values, String check_field, char cond_symbol, String cond)
				throws SQLException {
			Statement s = this.connect();

			String query = "UPDATE " + table + " SET " + values + " WHERE " + check_field + "" + cond_symbol + ""
					+ cond;
			// print(query);
			return s.executeUpdate(query);
		}

		int deleteField(String table, String check_field, char cond_symbol, String cond) throws SQLException {
			Statement s = this.connect();
			String query = "DELETE FROM " + table + " WHERE " + check_field + "" + cond_symbol + "" + cond;
			// print(query);
			return s.executeUpdate(query);

		}

		int addField(String table, String... values) throws SQLException {
			Statement s = this.connect();
			ResultSet rs = getAll(table);
			ResultSetMetaData data = rs.getMetaData();

			int count = data.getColumnCount();

			String set = "(";
			for (int i = 2; i <= count - 1; i++) {
				set += data.getColumnName(i) + ", ";
			}
			set += data.getColumnName(count) + ")";
			String val = "(";

			for (int i = 0; i < values.length - 1; i++)
				val += values[i] + ", ";
			val += values[values.length - 1] + ")";

			String query = "INSERT INTO " + table + "" + set + " VALUES" + val;
			// Main.print(query);
			return s.executeUpdate(query);
			//
		}

		private Statement connect() throws SQLException {
			Statement stmt = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/arknet", "hikari", "viadark")
					.createStatement();
			return stmt;
		}
	}

	public static void main(String[] args) {
		String platform = args[0];
		String fromid = args[1];
		String warp = args[2];
		String time = args[3];
		String message = args[4];
		long pr = 0;
		try {
			// pr = Long.parseLong(Crutch.fileRead("time.dat"));
			// long time_time = (Long.parseLong(time) - pr);
			long mytime = Long.parseLong(time);
			// double hours = (double)mytime/(60*60);
			// if( (Long.parseLong(time) - pr) );
			Updater.update((double) mytime / 3600, Long.parseLong(time), m);// опасная функция

			check(message, fromid, time);

		} catch (SQLException se) {
			System.out.println("sqlerror");
			se.printStackTrace();
		} catch (Exception e) {
			System.out.println("[IO] произошла ошибка");
			// e.printStackTrace();
		}
	}

	static void check(String arg, String fromid, String time) throws Exception {
		String[] sa = arg.split(" ");
		/*
		 * Main.print("parameters..."); for (String s : sa ) { Main.print(s); }
		 * 
		 * if(!(fromid.equals("326074388") || fromid.equals("461663203"))) {
		 * Main.print("[IO] нет прав"); return; }
		 */
		if (sa.length == 1) {
			switch (sa[0]) {
			// case "/filetest":
			// Crutch.fileOut("1.dat","123");
			// break;
			case "/crypto":
				print("🚪 Добро пожаловать в меню криптовалюты , основные команды: \n ✨ Создать криптовалюту /crypto create;"
						+ "\n 🛒 Купить криптовалюту с биржи /crypto buy"
						+ "\n 💰 Продать криптовалюту на биржу /crypto sell"
						+ "\n 💳 Перевести крипту другому игроку /crypto transact"
						+ "\n 👜 Проверить баланс на кошельке /crypto balance"
						+ "\n 📑 Краткий список криптовалют /crypto list"
						+ "\n 📜 Подробная информация о криптовалюте /crypto info" + "\n Управление компьютерами /pc"
						+ "\n 👷 Майнинг /mining" + "\n Объяснение устройства криптовалют /crypto help");
				break;
			case "/checker":
				print("check");
				break;
			case "/pc":
				print("💻 Добро пожаловать в систему управления ПК" + "\n 📑 Посмотреть список своих pc /pc list"
						+ "\n 📜 Подробная информацция о pc /pc info <id>" 
						+ "\n ✨ Создать компьютер /pc create" + "\n 🔧 Уничтожить компьютер /pc destroy <id>"
						+ "\n 📎 Магазин деталей /pc shop" + "\n 💰 Продать деталь в компьютере /pc selldetail "
						+ "\n 📊 Количество слотов для ПК /pc slots"
						+ "\n 💻 Подробнее o pc /pc help");
				break;
			case "/mining":
				print("₿ Добро пожаловать в систему управления майнингом:"
						+ "\n ⚕ Запустить компьютер в поток майнинга /mining set"
						+ "\n 💼 Собрать деньги с майнинга /mining profit " + "\n ❄ Выключить майнинг на ПК /mining leave"
						+ "\n 📜 Подробнее о майнинге /mining help");
				break;
			}
		} else {
			switch (sa[0]) {
			case "/crypto":
				switch (sa[1]) {
				case "help":
					Cryptocurrency.CryptoInfo();
					break;
				case "list":
					Cryptocurrency.list(m, true);
					break;
				case "create":
					if (sa.length == 5) {
						try {
							Cryptocurrency.create(Double.parseDouble(sa[3]) / 100, Long.parseLong(sa[4]), sa[2], fromid,
									m);
						} catch (Exception e) {
							Main.print("⚠ Произошла ошибка, укажите правильно параметры(имя, комиссия, фонд)");
							e.printStackTrace();
						}
					} else {
						Main.print("⚠ Укажите необходимые параметры /crypto create <name>(название криптовалют)"
								+ "\n <комиссия>(число от 0 до 99, допускаются десятичные дроби ) <фонд>(начальный фонд криптовалюты)");
					}
					break;
				case "sell":
					if (sa.length == 4) {
						try {
							Burse.sell(m, sa[2], Double.parseDouble(sa[3]), fromid);
						} catch (NullPointerException npe) {
						}
					} else {
						Main.print("⚠ Произошла ошибка, укажите правильно параметры (имя , сумма)");
					}
					break;
				case "buy":
					if (sa.length == 4) {
						try {
							Burse.buy(m, sa[2], Double.parseDouble(sa[3]), fromid);

						} catch (NullPointerException npe) {
						}
					} else {
						Main.print("⚠ Произошла ошибка, укажите правильно параметры (имя , сумма)");
					}
					break;
				case "info":
					if (sa.length == 3) {
						print(Cryptocurrency.getCryptoByName(sa[2], m));
					} else {
						Main.print("⚠ Произошла ошибка, укажите правильно параметры (имя)");
					}
					break;
				case "balance":
					try {
						if (sa.length == 3) {
							print("У вас на счету: "
									+ OutputManager.getFractionPrefix(Wallet
											.getWallet(Cryptocurrency.getCryptoByName(sa[2], m), fromid, m).getMoney())
									+ " " + sa[2] + "`s");
						} else {
							Main.print("⚠ Произошла ошибка, укажите правильно параметры (имя)");
						}
					} catch (Exception e) {
						print("⚠ Не удалось закончить");
						// e.printStackTrace();
					}
					break;
				case "transact":
					if (sa.length == 5) {
						Burse.transact(m, sa[2], Double.parseDouble(sa[3]) / 100, fromid, sa[4]);
					} else {
						Main.print("⚠ Произошла ошибка, укажите правильно параметры (имя , сумма, id игрока)");
					}
					break;

				}
				break;
			case "/pc":
				switch (sa[1]) {
				case "list":
					ArrayList<PCManager.PC> pclist = PCManager.getAllUserPC(fromid, m);
					if (pclist.isEmpty()) {
						Main.print("🔎 Компьютеров нет...");
					} else {
						for (PCManager.PC pc : pclist) {
							Main.print(pc.getShortPCInfo(m));
						}
					}
					break;
				case "info":
					if (sa.length == 3) {
						PCManager.PC pc = PCManager.PC.getPC(m, Integer.parseInt(sa[2]));
						if (pc != null) {
							PCManager.printPCInformation(Long.parseLong(sa[2]), fromid, m);
						}
					} else {
						Main.print("⚠ Ошибка , укажите правильно параметры (id)");
					}

					break;
				case "help":
					PCManager.PCInfo();
					break;
				case "create":
					try {
						print(PCManager.PC.createPC(m, fromid).getShortPCInfo(m));
					} catch (NullPointerException npe) {
					}
					break;
				case "destroy":
					if (sa.length == 3) {
						PCManager.PC.getPC(m, Integer.parseInt(sa[2])).destroy(m, fromid);
					} else {
						print("⚠ Укажите параметры (id)");
					}
					break;
				case "shop":
					if (sa.length == 2) {
						Main.print("======================\n МАГАЗИН ДЕТАЛЕЙ \n=======================\n"
								+ "1) Корпусы\n" + "2) Видеокарты\n" + "3) Материнские платы\n"
								+ "4) Блоки оперативной памяти\n" + "5) Процессоры\n" + "6) Винчестеры\n"
								+ "7) Бесперебойники\n" + "8) Устройства вывода\n" + "9) Кулеры\n"
								+ "10) Интернет-модули\n" + "Введите /pc shop <номер раздела> <страница>");
					} else if (sa.length >= 3) {
						String s, instruction;
						OutputManager bg = null;
						s = instruction = "";
						int page = 0;
						switch (Integer.parseInt(sa[2])) {
						case 1:

							s = "=======================\nРаздел Компьютерных корпусов\n=======================\n"
									+ "Добро пожаловать в раздел корпусов.Компьютерные корпусы используются для обеспечения\n"
									+ "целостности компьютеров , а более качественные уменьшают энергопотребление и увеличат\n"
									+ "безопасность.\n" + "                Сейчас в продаже:\n";

							bg = PCManager.getDetailListForType(Integer.parseInt(sa[2]),
									MESSAGE_SIZE - s.length() - 100, MESSAGE_SIZE - 100, m);
							page = 1;
							if ((sa.length == 4)) {
								page = Integer.parseInt(sa[3]);
								if (page > bg.getPartNumber()) {
									Main.print("[IO] Такой страницы не существует");
									return;
								}
							} else {
								print(s);
							}
							instruction = "\n для покупки введите /pc buydetail <id детали> <id компьютера>\n"
									+ "Страница " + page + " из " + bg.getPartNumber();
							Main.print(bg.getPart(page - 1));
							Main.print(instruction);
							break;
						case 2:
							s = "=======================\nРаздел Видеокарт\n=======================\n"
									+ "Добро пожаловать в раздел видеокарт.Видеокарты, один из самых важных элементов\n"
									+ "компьютера , они обеспечивают основную мощность майнинга, чем качественнее\n"
									+ "видеокарта, тем больше мощность, меньше энергопотребление и опасность поломки\n"
									+ "но бывают и исключения ;-)\n" + "                Сейчас в продаже:\n";

							bg = PCManager.getDetailListForType(Integer.parseInt(sa[2]),
									MESSAGE_SIZE - s.length() - 100, MESSAGE_SIZE - 100, m);
							page = 1;
							if ((sa.length == 4)) {
								page = Integer.parseInt(sa[3]);
								if (page > bg.getPartNumber()) {
									Main.print("🔎 такой страницы не существует");
									return;
								}
							} else {
								print(s);
							}
							instruction = "\n для покупки введите /pc buydetail <id детали> <id компьютера>\n"
									+ "Страница " + page + " из " + bg.getPartNumber();
							Main.print(bg.getPart(page - 1));
							Main.print(instruction);
							break;
						case 3:
							s = "=======================\nРаздел материнских плат\n=======================\n"
									+ "Добро пожаловать в раздел материнских плат.\n"
									+ "На качество майнинга они не особо влияют, но очень важны для работы ПК\n"
									+ "                Сейчас в продаже:\n";
							bg = PCManager.getDetailListForType(Integer.parseInt(sa[2]),
									MESSAGE_SIZE - s.length() - 100, MESSAGE_SIZE - 100, m);
							page = 1;
							if ((sa.length == 4)) {
								page = Integer.parseInt(sa[3]);
								if (page > bg.getPartNumber()) {
									Main.print("🔎 такой страницы не существует");
									return;
								}
							} else {
								print(s);
							}
							instruction = "\n для покупки введите /pc buydetail <id детали> <id компьютера>\n"
									+ "Страница " + page + " из " + bg.getPartNumber();
							Main.print(bg.getPart(page - 1));
							Main.print(instruction);
							break;
						case 4:
							s = "=======================\nРаздел ОЗУ\n=======================\n"
									+ "ОЗУ - оперативная память, деталь, отвечающая за быстродействие компьютера.\n"
									+ "Влияет на мощность майнинга\n" + "                Сейчас в продаже:\n";
							bg = PCManager.getDetailListForType(Integer.parseInt(sa[2]),
									MESSAGE_SIZE - s.length() - 100, MESSAGE_SIZE - 100, m);
							page = 1;
							if ((sa.length == 4)) {
								page = Integer.parseInt(sa[3]);
								if (page > bg.getPartNumber()) {
									Main.print("🔎 Такой страницы не существует");
									return;
								}
							} else {
								print(s);
							}
							instruction = "\n для покупки введите /pc buydetail <id детали> <id компьютера>\n"
									+ "Страница " + page + " из " + bg.getPartNumber();
							Main.print(bg.getPart(page - 1));
							Main.print(instruction);
							break;
						case 5:
							s = "=======================\nРаздел Процессоров\n=======================\n"
									+ "Добро пожаловать в раздел процессоров.Процессор — сердце компьютера! \n"
									+ "                Сейчас в продаже:\n";
							bg = PCManager.getDetailListForType(Integer.parseInt(sa[2]),
									MESSAGE_SIZE - s.length() - 100, MESSAGE_SIZE - 100, m);
							page = 1;
							if ((sa.length == 4)) {
								page = Integer.parseInt(sa[3]);
								if (page > bg.getPartNumber()) {
									Main.print("🔎 такой страницы не существует");
									return;
								}
							} else {
								print(s);
							}
							instruction = "\n для покупки введите /pc buydetail <id детали> <id компьютера>\n"
									+ "Страница " + page + " из " + bg.getPartNumber();
							Main.print(bg.getPart(page - 1));
							Main.print(instruction);
							break;
						case 6:
							s = "=======================\nРаздел Бесперебойников\n=======================\n"
									+ "Добро пожаловать в раздел бесперебойников.Бесперебойник — это внешняя деталь. \n"
									+ "Он хранит компьютер от перепадов тока , увеличивая срок его службы и уменьшая вероятность поломки.\n"

									+ "                Сейчас в продаже:\n";
							bg = PCManager.getDetailListForType(Integer.parseInt(sa[2]),
									MESSAGE_SIZE - s.length() - 100, MESSAGE_SIZE - 100, m);
							page = 1;
							if ((sa.length == 4)) {
								page = Integer.parseInt(sa[3]);
								if (page > bg.getPartNumber()) {
									Main.print("🔎 такой страницы не существует");
									return;
								}
							} else {
								print(s);
							}
							instruction = "\n для покупки введите /pc buydetail <id детали> <id компьютера>\n"
									+ "Страница " + page + " из " + bg.getPartNumber();
							Main.print(bg.getPart(page - 1));
							Main.print(instruction);
							break;
						case 7:
							s = "=======================\nРаздел Бесперебойников\n=======================\n"
									+ "Добро пожаловать в раздел бесперебойников.Бесперебойник — это внешняя деталь. \n"
									+ "Он хранит компьютер от перепадов тока , увеличивая срок его службы и уменьшая вероятность поломки.\n"

									+ "                Сейчас в продаже:\n";
							bg = PCManager.getDetailListForType(Integer.parseInt(sa[2]),
									MESSAGE_SIZE - s.length() - 100, MESSAGE_SIZE - 100, m);
							page = 1;
							if ((sa.length == 4)) {
								page = Integer.parseInt(sa[3]);
								if (page > bg.getPartNumber()) {
									Main.print("🔎 такой страницы не существует");
									return;
								}
							} else {
								print(s);
							}
							instruction = "\n для покупки введите /pc buydetail <id детали> <id компьютера>\n"
									+ "Страница " + page + " из " + bg.getPartNumber();
							Main.print(bg.getPart(page - 1));
							Main.print(instruction);
							break;
						case 8:
							s = "=======================\nРаздел Устройств вывода\n=======================\n"
									+ "Добро пожаловать в раздел устройств вывода.Устройства вывода нужны для взаимодействия. \n"
									+ "с компьютером.\n"

									+ "                Сейчас в продаже:\n";
							bg = PCManager.getDetailListForType(Integer.parseInt(sa[2]),
									MESSAGE_SIZE - s.length() - 100, MESSAGE_SIZE - 100, m);
							page = 1;
							if ((sa.length == 4)) {
								page = Integer.parseInt(sa[3]);
								if (page > bg.getPartNumber()) {
									Main.print("🔎 такой страницы не существует");
									return;
								}
							} else {
								print(s);
							}
							instruction = "\n для покупки введите /pc buydetail <id детали> <id компьютера>\n"
									+ "Страница " + page + " из " + bg.getPartNumber();
							Main.print(bg.getPart(page - 1));
							Main.print(instruction);
							break;
						case 9:
							s = "=======================\nРаздел кулеров\n=======================\n"
									+ "Добро пожаловать в раздел кулеров.Кулеры необходимы для разгона ПК. \n"
									+ "Положительно влияет на качество майнинга и уменьшает вероятность поломки.\n"

									+ "                Сейчас в продаже:\n";
							bg = PCManager.getDetailListForType(Integer.parseInt(sa[2]),
									MESSAGE_SIZE - s.length() - 100, MESSAGE_SIZE - 100, m);
							page = 1;
							if ((sa.length == 4)) {
								page = Integer.parseInt(sa[3]);
								if (page > bg.getPartNumber()) {
									Main.print("🔎 Такой страницы не существует");
									return;
								}
							} else {
								print(s);
							}
							instruction = "\n для покупки введите /pc buydetail <id детали> <id компьютера>\n"
									+ "Страница " + page + " из " + bg.getPartNumber();
							Main.print(bg.getPart(page - 1));
							Main.print(instruction);
							break;
						case 10:
							s = "=======================\nРаздел интернет-модулей\n=======================\n"
									+ "Добро пожаловать в раздел интернет-модулей.Нужны для вывода в сеть Интернет\n"
									+ "Увеличивает скорость связи с майнинговыми пулами, что увеличивает скорость майнинга.\n"

									+ "                Сейчас в продаже:\n";
							bg = PCManager.getDetailListForType(Integer.parseInt(sa[2]),
									MESSAGE_SIZE - s.length() - 100, MESSAGE_SIZE - 100, m);
							page = 1;
							if ((sa.length == 4)) {
								page = Integer.parseInt(sa[3]);
								if (page > bg.getPartNumber()) {
									Main.print("🔎 такой страницы не существует");
									return;
								}
							} else {
								print(s);
							}
							instruction = "\n для покупки введите /pc buydetail <id детали> <id компьютера>\n"
									+ "Страница " + page + " из " + bg.getPartNumber();
							Main.print(bg.getPart(page - 1));
							Main.print(instruction);
							break;
						default:
							Main.print("⛔ Такого раздела нет!");
							return;
						}
					}
					break;
				case "buydetail":
					try {
						print("🛒 Покупка деталей");
						if (sa.length == 3) {
							print("⚠ Укажите id компьютера");
						}
						if (sa.length == 2) {
							print("⚠ Укажите id детали , id компьютера");
						}
						if (sa.length == 4) {
							if (PCManager.buyDetail(Integer.parseInt(fromid), Integer.parseInt(sa[2]),
									PCManager.getDetailTypeId(Integer.parseInt(sa[2]), m),
									PCManager.PC.getPC(m, Integer.parseInt(sa[3])), m)) {
								print("⚠ Деталь установлена");
							} else {
								print("⛔ Покупка не удалась");
							}
						}
					} catch (NullPointerException n) {
					}
					break;
				case "selldetail":
					print("🔧 Продажа деталей");
					if (sa.length == 3) {
						print("⚠ Укажите id компьютера");
					}
					if (sa.length == 2) {
						print("⚠ Укажите номер слота детали , id компьютера");
					}
					if (sa.length == 4) {
						if (PCManager.sellDetail(Integer.parseInt(fromid),
								PCManager.getDetailTypeId(Integer.parseInt(sa[2]), m),
								PCManager.PC.getPC(m, Integer.parseInt(sa[3])), m)) {
							print("⚠ Деталь продана");
						} else {
							print("⛔ Продажа не удалась");
						}
					}
					break;
				case "slots":
					ResultSet r = m.getField("users", "home", "uid", '=', fromid);
					String home = "";
					if (r.next())
						home = r.getString(1);
					print("[IO] Свободных слотов:" + PCManager.getUserSlots(m, Long.parseLong(fromid)) + " всего:"
							+ PCManager.getHomeSlots(home));
					break;
				}
				break;
			case "/mining":
				switch (sa[1]) {
				case "set":
					if (sa.length == 2) {
						Main.print("⚠ Для запуска /mining set <id компьютера> <имя криптовалюты>");
					}
					if (sa.length == 3) {
						Main.print("⚠ Вы забыли указать имя криптовалюты");
					}
					if (sa.length == 4) {
						MiningManager.setMiningPool(fromid, Integer.parseInt(sa[2]), sa[3], Long.parseLong(time), m);
					}
					break;
				case "profit":
					if (sa.length == 2) {
						Main.print("⚠ Для сбора денег введите <id компьютера>");
					} else {
						MiningManager.getProfit(fromid, Integer.parseInt(sa[2]), Long.parseLong(time), m);
					}
					break;
				case "leave":
					if (sa.length == 2) {
						Main.print("⚠ Для выхода с потока  введите <id компьютера>");
					} else {
						MiningManager.leaveMiningPool(fromid, Integer.parseInt(sa[2]), Long.parseLong(time), m);
					}

					break;
				case "help":
					MiningManager.MiningInfo();
					break;
				}
				break;
			}

		}
	}

	static void print(boolean b) {
		System.out.println(b);
	}

	static void print(int i) {
		System.out.println(i);
	}

	static void print(ResultSet rs) {
		try {
			while (rs.next()) {
				Main.print("enter");
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++)
					System.out.println(rs.getString(i));
			}

		} catch (SQLException se) {
			se.printStackTrace();
			System.out.println("SQLERROR");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ERROR");
			// 1
		}
	}

	static void print(String... a) {
		for (String s : a) {
			System.out.println(s);
		}
	}

	static void print(Object... o) {
		for (Object x : o) {
			System.out.println(x);
		}

	}

}

class Crutch {

	public static double getMantissa(double r) {
		if (r == 0) {
			return 0;
		}
		if (Math.abs(r) < 1) {
			while (Math.abs(r) < 1) {
				r *= 10;
			}
		} else {
			while (Math.abs(r) > 10) {
				r /= 10;
			}
		}
		return r;
	}

	static boolean fileOut(String name, String data) throws Exception {
		FileWriter f = new FileWriter(name, false);
		f.write(data);
		f.close();
		return true;
	}

	static String fileRead(String name) throws Exception {
		FileReader fr = new FileReader(name);
		Scanner sc = new Scanner(fr);
		String ret = "";
		while (sc.hasNextLine()) {
			ret += sc.nextLine();
			fr.close();
		}
		return ret;
	}

}

class Updater {

	public static void update(double timeinh, long time, Main.Mysql m) throws SQLException {
		double update = timeinh - getLastUpdateTime(m);
		if (update > 0.5) {
			Burse.updateAll(m, update);
			setLastUpdateTime(time, m);
			Main.print("⚠ Цены на криптовалюты были обновлены");
		}
	}

	/*
	 * if(update > 8){ // }
	 */
	private static double getLastUpdateTime(Main.Mysql m) throws SQLException {
		ResultSet r = m.getField("meta", "value", "name", '=', "'lasttime'");
		double result = 0;
		if (r.next()) {
			result = (double) r.getLong(1) / 3600;
			return result;
		}

		return 0.0;
	}

	private static boolean setLastUpdateTime(long time, Main.Mysql m) throws SQLException {
		m.setField("meta", "value=" + time, "name", '=', "'lasttime'");
		return true;
	}
}

class OutputManager {
	private ArrayList<String> parts;
	
	public OutputManager(){
		this.parts = new ArrayList<String>(10);
	}
	
	public void addPart(String s){
		parts.add(s);
	}
	
	public int getPartNumber(){
		return parts.size();
	}
	
	public String getPart (int index) {
		return parts.get(index);
	}
	
	public static String getFractionPrefix(double r) {
		if(r>1){
			return r+"";
		}else if(r>0.001){
			return (r*1000) + " тысячных ";
		}else if(r>0.000001){
			return(r*1000000)+ " миллионных ";
		}else if(r>0.000000001){
			return(r*1000000000) + " миллиардных ";
		}else if(r==0){
			return r+"";
		}
		else return "слишком маленькое число ";
	}
	


}