import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
  private static final Workbook WORKBOOK = new HSSFWorkbook();

  public static void main(String[] args) {
    int n = 10000;

    Scanner scanner = new Scanner(System.in);
    System.out.println("1: Normal distribution 1");
    System.out.println("2: Normal distribution 2");
    System.out.println("3: Exponential distribution");
    System.out.println("4: Chi-square distribution");
    System.out.println("5: Student distribution");
    System.out.println("\nInput number of distribution: ");
    int my_case = scanner.nextInt();
    switch (my_case) {
      case 1 -> distribution(n, "Normal distribution 1");
      case 2 -> distribution(n, "Normal distribution 2");
      case 3 -> distribution(n, "Exponential distribution");
      case 4 -> distribution(n, "Chi-square distribution");
      case 5 -> distribution(n, "Student distribution");
      default -> System.out.println("Incorrect number");
    }

    try {
      WORKBOOK.write(new FileOutputStream("k.xls"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void distribution(int n, String distribution) {
    List<Double> randomNumbers = new ArrayList<>(n);
    switch (distribution) {
      case "Normal distribution 1": {
        for (int i = 0; i < n; i++) {
          randomNumbers.add(RNRM1());
        }
        break;
      }
      case "Normal distribution 2": {
        for (int i = 0; i < n; i++) {
          randomNumbers.add(RNRM2());
        }
        break;
      }
      case "Exponential distribution": {
        for (int i = 0; i < n; i++) {
          randomNumbers.add(RNEXP(1));
        }
        break;
      }
      case "Chi-square distribution": {
        for (int i = 0; i < n; i++) {
          randomNumbers.add(RNCHIS());
        }
        break;
      }
      case "Student distribution": {
        for (int i = 0; i < n; i++) {
          randomNumbers.add(RNSTUD());
        }
        break;
      }
    }

    double rUp = randomNumbers.stream().max(Double::compareTo).get();
    double lLow = randomNumbers.stream().min(Double::compareTo).get();

    System.out.println("\n" + distribution);
    Sheet sheet = WORKBOOK.createSheet(distribution);
    graphics(lLow, rUp, n, sheet, randomNumbers);
    evaluation(n, randomNumbers);
  }

  private static void graphics(double lLow, double rUp, int n, Sheet sheet, List<Double> randomNumbers) {
    List<Double> probability = new ArrayList<>(10);
    List<Double> probability2 = new ArrayList<>(10);

    int k = 10;
    double h = (rUp - lLow) / 10;

    for (int i = 0; i < k; i++) {
      probability.add((double) 0);
      probability2.add((double) 0);
    }
    for (int i = 0; i < n; i++) {
      int index = (int) Math.round((randomNumbers.get(i) - lLow) / h) == k ? k - 1 : (int) Math.round((randomNumbers.get(i) - lLow) / h);
      probability2.set(index, probability2.get(index) + 1);
      for (int j = index; j < k; j++) {
        probability.set(j, probability.get(j) + 1);
      }
    }

    for (int i = 0; i < k; i++) {
      probability.set(i, probability.get(i) / n);
      probability2.set(i, probability2.get(i) / n);
    }

    List<Cell> cells = new ArrayList<>(3);
    for (int i = 0; i < k; i++) {
      Row row = sheet.createRow(i);
      cells.add(row.createCell(0));
      cells.add(row.createCell(1));
      cells.add(row.createCell(2));
      cells.get(0).setCellValue(probability.get(i));
      cells.get(1).setCellValue(probability2.get(i));
      cells.get(2).setCellValue(lLow + i * h);
      cells.clear();
    }
  }

  private static void evaluation(int n, List<Double> randomNumbers) {
    double sum = 0;
    for (int i = 0; i < n; i++) {
      sum += randomNumbers.get(i);
    }
    double matheExpectation = sum / n;

    double dispersion = 0;
    for (int i = 0; i < n; i++) {
      dispersion += Math.pow(randomNumbers.get(i) - matheExpectation, 2);
    }
    dispersion /= n;

    System.out.println("n: " + n +
        "\nMath expectation: " + matheExpectation +
        "\nDispersion: " + dispersion);
  }

  private static double RNRM1() {
    return Math.sqrt(-2 * Math.log(Math.random())) * Math.cos(2 * Math.PI * Math.random());
  }

  private static double RNRM2() {
    double sum = 0;
    for (int i = 0; i < 12; ++i) {
      sum += Math.random();
    }
    return sum - 6;
  }

  private static double RNEXP(int b) {
    return -b * Math.log(Math.random());
  }

  private static double RNCHIS() {
    double sum = 0;
    for (int i = 0; i < 10; ++i) {
      sum += Math.pow(RNRM1(), 2);
    }
    return sum;
  }

  private static double RNSTUD() {
    return RNRM1() / Math.sqrt(RNCHIS() / 10);
  }
}
