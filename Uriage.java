package jp.alhinc.sasaki_hiromu.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Uriage {
	public static void main(String[] args) {
		HashMap<String, String> branchShop = new HashMap<String, String>() ;
		HashMap<String, Long> branchSum = new HashMap<String, Long>() ;
		try {
			File file = new File(args[0], "branch.lst");
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String s;
			while((s = br.readLine()) != null) {
				String[] shops = s.split(",", 0);
				if (shops[0].matches("^\\d{3}")) {
					branchShop.put(shops[0], shops[1]);
					branchSum.put(shops[0], 0L);
				} else {
					System.out.println("支店定義ファイルのフォーマットが不正です");
					br.close();
					return;
				}
			}
			br.close();
		} catch(IOException e) {
			System.out.println("支店定義ファイルが存在しません");
			return;
		}//System.out.println(branchSum.entrySet());
		HashMap<String, String> commodityBy = new HashMap<String, String>() ;
		HashMap<String, Long> commoditySum = new HashMap<String, Long>() ;
		try {
			File file = new File(args[0], "commodity.lst");
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String s;
			while((s = br.readLine()) != null) {
				String[] masins = s.split(",", 0);
				if (masins[0].matches("^\\w{8}")) {
					commodityBy.put(masins[0], masins[1]);
					commoditySum.put(masins[0], 0L);
				} else {
					System.out.println("商品定義ファイルのフォーマットが不正です");
					br.close();
					return;
				}
			}
			br.close();
		} catch(IOException e) {
			System.out.println("商品定義ファイルが存在しません");
			return;
		}
		File dir = new File(args[0]);
		ArrayList<File> file = new ArrayList<File>();
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			String filename = files[i].getName();
			//String new_filename = filename.substring(1,8);
			//System.out.println(filename);
			if (filename.matches("^\\d{8}.rcd")) {
				//String new_filename = filename.substring(1,8);
				//System.out.println(filename);
				file.add(files[i]);
			}
		}
		for (int i = 0; i < file.size(); i++) {
			String filename = file.get(i).getName();
			String name = filename.substring(1,8);
			int number = Integer.parseInt(name);
			//System.out.println();
			if (!(number == i + 1)) {
				System.out.println("売上ファイル名が連番になっていません");
				return;
			}
		}
		ArrayList<Map.Entry<String,Long>> maxMin = new ArrayList<Map.Entry<String,Long>>(branchSum.entrySet());
		ArrayList<Map.Entry<String,Long>> saleMax = new ArrayList<Map.Entry<String,Long>>(commoditySum.entrySet());
		try {
			for (int i = 0; i < file.size(); i++) {
				BufferedReader br = new BufferedReader(new FileReader(file.get(i)));
				ArrayList<String> payList = new ArrayList<String>();
				try {
					String line;
					while ((line  = br.readLine()) != null) {
						payList.add(line);
					}
					if (!(payList.size() == 3)) {
						System.out.println(file.get(i).getName() + "のフォーマットが不正です");
						br.close();
						return;
					}
					if (branchSum.containsKey(payList.get(0))) {
						Long cord = branchSum.get(payList.get(0));
						cord += Long.parseLong(payList.get(2));
						branchSum.put(payList.get(0), cord);
					} else {
						System.out.println(file.get(i).getName() + "の支店コードが不正です");
						br.close();
						return;
					}
					if (commoditySum.containsKey(payList.get(1))) {
						Long sale = commoditySum.get(payList.get(1));
						sale += Long.parseLong(payList.get(2));
						commoditySum.put(payList.get(1), sale);
					} else {
						System.out.println(file.get(i).getName() + "の商品コードが不正です");
						br.close();
						return;
					}
					Long branchLast = branchSum.get(payList.get(0));
					//System.out.println(s);
					if (branchLast < 9999999999L) {
					} else {
						System.out.println(file.get(i).getName() + "のフォーマットが不正です");
						br.close();
						return;
					}
					br.close();
				}
				catch(FileNotFoundException e) {
				}
			}
			//ArrayList<Map.Entry<String,Long>> maxMin = new ArrayList<Map.Entry<String,Long>>(branchSum.entrySet());
			Collections.sort(maxMin, new Comparator<Map.Entry<String,Long>>() {
				public int compare(
						Entry<String,Long> maxMin1, Entry<String,Long> maxMin2) {
					return ((Long)maxMin2.getValue()).compareTo((Long)maxMin1.getValue());
				}
			});
			Collections.sort(saleMax, new Comparator<Map.Entry<String,Long>>() {
				public int compare(
						Entry<String,Long> saleMax1, Entry<String,Long> saleMax2) {
					return ((Long)saleMax2.getValue()).compareTo((Long)saleMax1.getValue());
				}
			});
			//System.out.println(branchSum.entrySet());
			//System.out.println(branchSum.values());
			//System.out.println(commoditySum.entrySet());
			//System.out.println(commoditySum.values());
		}
		catch( IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;

		}
		File branchOut = new File(args[0], "branch.out");
		File commodityOut = new File(args[0], "commodity.out");
		try {
			branchOut.createNewFile();
			FileWriter fw = new FileWriter(branchOut);
			BufferedWriter bw = new BufferedWriter(fw);
			for (Entry<String,Long> branchSort : maxMin) {
				bw.write(branchSort.getKey() + ","  + branchShop.get(branchSort.getKey()) + "," +  branchSort.getValue() + "\n");
			}
			bw.close();
		} catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;

		}
		try {
			commodityOut.createNewFile();
			FileWriter fw = new FileWriter(commodityOut);
			BufferedWriter bw = new BufferedWriter(fw);
			for (Entry<String,Long> commoditySort : saleMax) {
				bw.write(commoditySort.getKey() + ","  + commodityBy.get(commoditySort.getKey()) + "," +  commoditySort.getValue() + "\n");
			}
			bw.close();
		} catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
	}
}