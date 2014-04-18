MiniDownloader
==============

Mali download menadžer. Dozvoljava preuzimanje jedne datoteke preko Interneta. Datoteci se pristupa upisivanjem njenog URL-a. Iako radi, program može da se unaprijedi. Moguća unaprijeđenja:
1. Program datoteku preuzima i učitava je u niz bajtova. Datoteka se čuva u RAM-u, do kraja preuzimanja, kada se dopusti korisniku da preuzete bajtove sačuva u datoteku na lokalnom sistemu. Ovo je u redu, kada se radi o malim datotekama. Što je preuzeta datoteka veća, program će zauzeti više RAM-a, što može da predstavlja veliki problem kod velikih datoteka. Napraviti tako da, do određene granice veličine datoteke (recimo 64MB) program radi kako radi i do sad, a u suprotnom da preuzete bajtove upisuje u privremenu datoteku (http://docs.oracle.com/javase/7/docs/api/java/io/File.html#createTempFile%28java.lang.String,%20java.lang.String%29), pa nakon toga iz nje sačuva u datoteku na lokalnom sistemu koju izabere korisnik, isto kao i do sad.
2. Napraviti da program preuzima isključivo slike. Podržati formate: JPG, PNG, BMP, GIF i TIFF.
3. Pustite mašti na volju...
