package fr.asmathieu.licence;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import io.reactivex.Observable;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.RequestOptions;

public class LicenceExtractor {

	private static final String LICENCE_PATH = "/extrafoot/EX_LICENCES.Licences?";
	private static HttpClient client;

	private static final String LICENCE_ID_PATTERN = ".*selectPersonne\\('(\\d*)'\\).*";
	private static final String LICENCE_LAST_NAME_PATTERN = ".*selectPersonne.*\".([A-Z\\-]*) .*";
	private static final String LICENCE_FIRST_NAME_PATTERN = ".*selectPersonne.*\".[A-Z]* (.*)./a.*";
	private static final String BIRTH_DATE_PATTERN = ".*(\\d{2}/\\d{2}/\\d{4}).*";
	private static final String REGISTER_DATE_PATTERN = ".*selectLicence.*(\\d{2}/\\d{2}/\\d{4}).*";
	private static final String LICENCE_CATEGORY_PATTERN = ".*profilIndividuReload\\('\\d*','(.{1,40})'\\);\".*";
	private static final String LICENCE_STATE_PATTERN = ".*setTitle.*>(.*)</a>.*"; // FIXME
																					// this
																					// pattern
																					// doesn't
																					// work
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	public static void main(String[] args) throws FileNotFoundException, IOException {

		Properties properties = new Properties();
		try (InputStream inputStream = LicenceExtractor.class.getResourceAsStream("/credentials.properties")) {
			properties.load(inputStream);
		}
		String licenceQueryParam = properties.getProperty("licence.queryParam");

		Vertx vertx = Vertx.vertx();
		client = vertx.createHttpClient();
		LicenceExtractor licenceExtractor = new LicenceExtractor();

		licenceExtractor.login(properties.getProperty("username"), properties.getProperty("password"))
				.flatMap(response -> licenceExtractor.retrieveLicencies(response.headers()
						.get("Set-Cookie"), licenceQueryParam))
				.flatMap(licenceExtractor::toLicences)
				.subscribe(System.out::println, System.out::println, () -> {
					System.out.println("complete");
					vertx.close();
				});

	}

	private Observable<Licence> toLicences(String body) {
		return Observable.create(sub -> {
			try {
				Document doc = Jsoup.parse(body);
				Element table = doc.getElementById("EXT_CLB_LIC_LIST_GRID");
				Element tBody = table.getElementsByTag("tbody")
						.first();
				Elements rows = tBody.getElementsByTag("tr");
				rows.parallelStream()
						.filter(element -> !element.className()
								.equals("backmenu"))
						.map(this::extractLicence)
						.filter(Objects::nonNull)
						.forEach(sub::onNext);
				sub.onComplete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private Licence extractLicence(Element trElement) {
		try {
			String id = extractLicenceInfo(trElement, LICENCE_ID_PATTERN);
			String lastName = extractLicenceInfo(trElement, LICENCE_LAST_NAME_PATTERN);
			String firstName = extractLicenceInfo(trElement, LICENCE_FIRST_NAME_PATTERN);
			LocalDate birthDate = LocalDate.parse(extractLicenceInfo(trElement, BIRTH_DATE_PATTERN), DATE_FORMATTER);
			LocalDate registerDate = LocalDate.parse(extractLicenceInfo(trElement, REGISTER_DATE_PATTERN), DATE_FORMATTER);
			LicenceCategory licenceCategory = LicenceCategory.getEnum(extractLicenceInfo(trElement, LICENCE_CATEGORY_PATTERN));
			LicenceState licenceState = LicenceState.VALIDATED;/*
																 * LicenceState.
																 * getEnum(
																 * extractLicenceInfo
																 * (trElement,
																 * LICENCE_STATE_PATTERN
																 * ));
																 */
			return new Licence(firstName, lastName, id, birthDate, registerDate, licenceCategory, licenceState);
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
			return null;
		}
	}

	private String extractLicenceInfo(Element trElement, String pattern) throws Exception {
		return trElement.getElementsByTag("td")
				.stream()
				.filter(tdElement -> tdElement.toString()
						.matches(pattern))
				.findFirst()
				.map(tdElement -> {
					Matcher matcher = Pattern.compile(pattern)
							.matcher(tdElement.toString());
					if (matcher.matches()) {
						return matcher.group(1);
					} else {
						throw new IllegalStateException(tdElement.toString() + " doesn't match " + LICENCE_ID_PATTERN);
					}
				})
				.orElseThrow(() -> new Exception("Pattern '" + pattern + "' not found"));
	}

	private Observable<HttpClientResponse> login(String username, String password) {
		String body = "p_username=" + username + "&p_password=" + password + "&app=FOOTCLUBS";
		return Observable.create(sub -> {
			client.post(new RequestOptions().setPort(443)
					.setHost("footclubs.fff.fr")
					.setURI("/extrafoot/SYM_LOGIN.goConnect")
					.setSsl(true), response -> {
						sub.onNext(response);
						sub.onComplete();
					})
					.end(body);
		});
	}

	private Observable<String> retrieveLicencies(String cookie, String queryParam) {
		return Observable.create(sub -> client.get(new RequestOptions().setPort(443)
				.setHost("footclubs.fff.fr")
				.setURI(LICENCE_PATH + queryParam)
				.setSsl(true), response -> {
					response.bodyHandler(buffer -> {
						String body = new String(buffer.getBytes(), Charset.forName("ISO-8859-15"));
						sub.onNext(body);
						sub.onComplete();
					});
				})
				.putHeader("Cookie", cookie)
				.end());
	}

}
