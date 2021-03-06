package de.lmu.cis.ocrd.align.test;

import de.lmu.cis.ocrd.align.TokenAlignment;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TokenAlignmentTest extends de.lmu.cis.ocrd.test.Test {
	@Before
	public void init() {
		enableDebugging();
	}

	@Test
	public void test2alignments() {
		final String a = "abc def";
		final String b = "a b c def";
		TokenAlignment tokens = new TokenAlignment(a).add(b);
		assertThat(tokens.size(), is(2));
		assertThat(tokens.get(0).toString(), is("abc|a,b,c"));
		assertThat(tokens.get(1).toString(), is("def|def"));
	}

	@Test
	public void test3alignments() {
		final String a = "abcd ef gh";
		final String b = "ab cd efgh";
		final String c = "ab cd ef gh";
		TokenAlignment tokens = new TokenAlignment(a).add(b).add(c);
		assertThat(tokens.size(), is(3));
		assertThat(tokens.get(0).toString(), is("abcd|ab,cd|ab,cd"));
		assertThat(tokens.get(1).toString(), is("ef|efgh|ef"));
		assertThat(tokens.get(2).toString(), is("gh|efgh|gh"));
	}

	@Test
	public void testAlignToSelf() {
		final String a = "abc def ghi";
		TokenAlignment tokens = new TokenAlignment(a).add(a);
		assertThat(tokens.size(), is(3));
		assertThat(tokens.get(0).toString(), is("abc|abc"));
		assertThat(tokens.get(1).toString(), is("def|def"));
		assertThat(tokens.get(2).toString(), is("ghi|ghi"));
	}

	@Test
	public void testBug1() {
		final String a = "nen in dem Momente wo der Wagenzug anrasselte die gräßliche Zerschmettere er";
		final String b = "nen in de m Momente wo der Wagenzug anrasseltez die CgräßlichksZekschmktkckking ex";
		TokenAlignment tokens = new TokenAlignment(a).add(b);
		assertThat(tokens.size(), is(12));
		assertThat(tokens.get(0).toString(), is("nen|nen"));
		assertThat(tokens.get(3).toString(), is("Momente|Momente"));
		assertThat(tokens.get(6).toString(), is("Wagenzug|Wagenzug"));
	}

	@Test
	public void testBug2() {
		final String a = "first second ThIrD ALONGTOKEN anotherevenlongertoken";
		final String b = "First second third alongtoken anotherevenlongertoken";
		final String c = "First second third alongtoken anotherevnlongertkn";
		final String d = "frst second third alongtoken anotherevnlongertkn";
		final TokenAlignment tokens = new TokenAlignment(a).add(b).add(c).add(d);
		assertThat(tokens.size(), is(5));
		assertThat(tokens.get(0).toString(), is("first|First|First|frst"));
		assertThat(tokens.get(1).toString(), is("second|second|second|second"));
		assertThat(tokens.get(2).toString(), is("ThIrD|third|third|third"));
		assertThat(tokens.get(3).toString(), is("ALONGTOKEN|alongtoken|alongtoken|alongtoken"));
		assertThat(tokens.get(4).toString(), is("anotherevenlongertoken|anotherevenlongertoken|anotherevnlongertkn|anotherevnlongertkn"));
	}

	@Test
	public void testBug3() {
        TokenAlignment tokens;
		final String a = "geſgenen eentraliſirten Volkes fragt die alten Städte Gent Briggr";
		final String b = "gesogenen centralisirtenVollesz fragt die alten Städte Gent Brügge";
        final String c = "gesogcnen centralisirten Volkes fragt die alten Städte Gent Brügge";
        tokens = new TokenAlignment(a).add(b);
        assertThat(tokens.size(), is (9));
        tokens = new TokenAlignment(a).add(c);
		assertThat(tokens.size(), is(9));
        tokens = new TokenAlignment(a).add(b).add(c);
        assertThat(tokens.size(), is(9));
        tokens = new TokenAlignment(a).add(c).add(b);
        assertThat(tokens.size(), is(9));
	}

	@Test
	public void testBug4() {
		final String a = "je nachdem sich die vorbildenden und die entscheidenden Perioden die Zeiten";
		final String b = "je nachdem sich die dorbildenden und die entscheidenden Perioden sdie Zeiten";
//		System.out.println(new Graph(a, b).getStartNode());
		final TokenAlignment tokens = new TokenAlignment(a).add(b);
		assertThat(tokens.size(), is(11));
	}

	@Test
	public void testBug5() {
		final String a = "che die schönste Blüthe der dramatischen Krone Spaniens sind Das Zeitalter";
		final String b = "che die ſchöͤnſte Blüthe der dramatiſchen sKkrone Spaniens ſind Das Zeitalter";
		final TokenAlignment tokens = new TokenAlignment(a).add(b);
		assertThat(tokens.size(), is(11));
	}

	@Test
	public void testBug6() {
		final String a = "Nach ahmunasm ürdig";
		final String b = "Na ch a h m u n g s w ii r di a l";
		final TokenAlignment tokens = new TokenAlignment(a).add(b);
		assertThat(tokens.size(), is(3));
		assertThat(tokens.get(0).toString(), is("Nach|Na,ch"));
		assertThat(tokens.get(1).toString(), is("ahmunasm|a"));
		assertThat(tokens.get(2).toString(), is("ürdig|h,m,u,n,g,s,w,ii,r,di,a,l"));
	}

	@Test
	public void testBug7() {
		final String a = "thig mit dem Rücken ihm zugekehrt Und wahrlich es ist nicht gut daß es";
		final String b = "tbig mit dem Mcken i zulgefebrt Und wahrlich es iſt nicht guut daß es";
		final TokenAlignment tokens = new TokenAlignment(a).add(b);
		assertThat(tokens.size(), is(14));
		assertThat(tokens.get(12).toString(), is("daß|daß"));
		assertThat(tokens.get(13).toString(), is("es|es"));
	}
}
