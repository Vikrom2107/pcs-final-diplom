import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    private Map<String, List<PageEntry>> generalSearch = new HashMap<>();
    private List<String> stopWords = new ArrayList<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        // прочтите тут все pdf и сохраните нужные данные,
        // тк во время поиска сервер не должен уже читать файлы
        if (pdfsDir.isDirectory() && pdfsDir.exists()) {
            for (File pdf : pdfsDir.listFiles()) {
                var doc = new PdfDocument(new PdfReader(pdf));
                // Проходим по всем страницам файла для поиска совпадений
                for (int i = 1; i <= doc.getNumberOfPages(); i++) {
                    String text = PdfTextExtractor.getTextFromPage(doc.getPage(i));
                    String[] words = text.split("\\P{IsAlphabetic}+");
                    Map<String, Integer> freqs = new HashMap<>();
                    for (String word : words) {
                        if (word.isEmpty()) {
                            continue;
                        }
                        word = word.toLowerCase();
                        freqs.put(word, freqs.getOrDefault(word, 0) + 1);
                    }
                    // Кладём значения из мапы слов с количеством совпадений в общую мапу с PageEntry
                    for (Map.Entry<String, Integer> entry : freqs.entrySet()) {
                        if (!generalSearch.containsKey(entry.getKey())) {
                            generalSearch.put(entry.getKey(), new ArrayList<>());
                            generalSearch.get(entry.getKey())
                                    .add(new PageEntry(pdf.getName(), i, entry.getValue()));
                        } else {
                            generalSearch.get(entry.getKey())
                                    .add(new PageEntry(pdf.getName(), i, entry.getValue()));
                        }
                    }
                }
            }
        }
        // Вытаскиваем слова из стоп-листа в массив
        File txtFile = new File("stop-ru.txt");
        if (txtFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(txtFile))) {
                String line = null;
                while ((line = br.readLine()) != null) {
                    stopWords.add(line);
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
    public List<PageEntry> searchWithStopWords(String query) {
        String [] words = query.split(" ");
        if (words.length <= 1)
            return search(query);
        else {
            List<PageEntry> result = new ArrayList<>();
            for (String word : words) {
                // Проверка, есть ли слово в стоп-листе
                if (stopWords.contains(word))
                    continue;

                if (result.isEmpty()) {
                    result.addAll(search(word));
                    continue;
                }
                // Сверяем результаты поиска разных слов, и если совпали файлы и страницы, то увеличиваем количество,
                // иначе просто добавляем новый результат поиска в result
                List<PageEntry> wordResult = search(word);
                List<PageEntry> wordResultNotEqual = new ArrayList<>();
                for (PageEntry p1 : wordResult) {
                    for (PageEntry p2 : result) {
                        if (p1.getPdfName().equals(p2.getPdfName()) && p1.getPage() == p2.getPage())
                            p1.setCount(p1.getCount() + p2.getCount());

                        if (!wordResultNotEqual.contains(p1))
                            wordResultNotEqual.add(p1);
                    }
                }
                result.addAll(wordResultNotEqual);
            }
            Collections.sort(result);
            return result;
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        if (generalSearch.containsKey(word)) {
            Collections.sort(generalSearch.get(word));
            return generalSearch.get(word);
        }
        return Collections.emptyList();
    }
}
