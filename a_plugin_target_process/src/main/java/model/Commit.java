package model;

/**
 * @since 2.1
 */
public class Commit {

	String author;
	String date;
	String message;

	public Commit(String string) {
		author = string.split("Author: ")[1].split("\n")[0].trim();
		String[] dateNCommit = string.split("Date:")[1].split("\n", 2);
		date = dateNCommit[0].trim();
		message = dateNCommit[1].trim();
	}

	public String getAuthor() {
		return author;
	}

	public String getMessage() {
		return message;
	}

}
