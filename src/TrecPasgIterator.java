import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

public class TrecPasgIterator implements Iterator<Document> {

    protected BufferedReader rdr;
    protected boolean at_eof = false;

    public TrecPasgIterator(File file) throws FileNotFoundException {
        rdr = new BufferedReader(new FileReader(file));
        System.out.println("Reading " + file.toString());
    }

    @Override
    public boolean hasNext() {
        return !at_eof;
    }

    @Override
    public Document next() {
        Document doc = new Document();
        StringBuffer sb = new StringBuffer();
        try {
            String line;
            Pattern docno_tag = Pattern.compile("<DOCNO>\\s*(\\S+)\\s*<");
            boolean in_doc = false;
            while (true) {
                line = rdr.readLine();
                if (line == null) {
                    at_eof = true;
                    break;
                }
                if (!in_doc) {
                    if (line.startsWith("<DOC>"))
                        in_doc = true;
                    else
                        continue;
                }
                if (line.startsWith("</DOC>")) {
                    in_doc = false;
                    sb.append(line);
                    break;
                }

                Matcher m = docno_tag.matcher(line);
                if (m.find()) {
                    String docno = m.group(1);
                    doc.add(new StringField("docno", docno, Field.Store.YES));
                }

                sb.append(line);
            }
            if (sb.length() > 0)
                doc.add(new TextField("contents", sb.toString(), Field.Store.NO));

        } catch (IOException e) {
            doc = null;
        }
        return doc;
    }

    public ArrayList<Document> nextPsg() {
        ArrayList<Document> psgs = new ArrayList<Document>();
        Document doc = new Document();
        StringBuffer psb = new StringBuffer();
        try {
            String line;
            Pattern docno_tag = Pattern.compile("<DOCNO>\\s*(\\S+)\\s*<");
            boolean in_doc = false;
            boolean in_psg = false;
            boolean in_title = false;
            String docno="";
            String title="";
            int psgno = 1;
            while (true) {
                line = rdr.readLine();
                if (line == null) {
                    at_eof = true;
                    break;
                }
                if (!in_doc) {
                    if (line.startsWith("<DOC>"))
                        in_doc = true;
                    else
                        continue;
                }
                if (line.startsWith("</DOC>")) {
                    in_doc = false;
                    break;
                }
                if (line.startsWith("<HEADLINE>")) {
                    in_title = true;
                    continue;
                }
                if (line.startsWith("</HEADLINE>")) {
                    in_title = false;
                }
                if (in_title) {
                    title = line;
                }
                if (line.startsWith("<TEXT>")) {
                    in_psg = true;
                    continue;
                }

                if (line.startsWith("</TEXT>")) {
                    in_psg = false;
                }

                Matcher m = docno_tag.matcher(line);
                if (m.find()) {
                    docno = m.group(1);
                }

                if(in_psg){
                    psb.append(line);
                    if(line.endsWith(".")){
                        doc.add(new StringField("docno", docno + " psg:" + psgno, Field.Store.YES));
                        doc.add(new StringField("title", title, Field.Store.YES));
                        //System.out.println(doc.get("docno"));
                        psgno++;
                        if (psb.length() > 0){
                            doc.add(new TextField("contents", psb.toString(), Field.Store.YES));
                        //    System.out.println(doc.get("contents"));
                        //    System.out.println();
                        }
                        psgs.add(doc);
                        doc = new Document();
                        psb = new StringBuffer();
                    }
                }

            }
        } catch (IOException e) {
            psgs = null;
        }
        return psgs;
    }

    @Override
    public void remove() {
        // Do nothing, but don't complain
    }

}
