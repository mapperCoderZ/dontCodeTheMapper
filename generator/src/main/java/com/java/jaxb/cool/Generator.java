package com.java.jaxb.cool;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * English at the end !
 * </p>
 * <p>
 * Classe UTILITAIRE qui genere le squelette du code de mapping, et les
 * affectations de variables evidentes, d'un type annoté JAXB vers un autre type
 * annoté JAXB. Génère aussi un joli rapport en HTML. Les affecttations de
 * variables évidentes sont calculées à partie d'un algo simple de similarité,
 * basé sur les types, les nom de champs, et quelques balises JaxB.
 * </p>
 * <p>
 * UTILITY Tool that allows to generate Java mapping code from a JAXB annotated
 * type to another based on JAXB interfaces. It's based on a similarity simple
 * algorithm that writes java new, sets, if null and gets directly
 * (assignments), when it's easy. It generates a small report in HTML that
 * allows to validate visually the mapping. Sorry for this bad english. Have
 * fun.
 * </p>
 * <p>
 * 
 * This code was written in one day because writting java mapping code sucks,
 * please be cool! KISS.
 * </p>
 * <p>
 * Somme limits: does not manage Collections and complex types which are not
 * XmlType (javax), field name "serialVersionUID" is ignored...
 * </p>
 * 
 * @author GillesOFraisse
 */
public class Generator {
	/* come back later */
	public static Map<String, Integer> mapIndexByName = new HashMap<String, Integer>();

	/**
	 * Generate the Java mapping code (creates, sets and gets, if null),
	 * toString and HTML report.
	 * 
	 * @param a
	 *            source type
	 * @param b
	 *            target type
	 * @param generateUnsetFields
	 *            true to generate the unset fields (when algorithm cannot
	 *            determinate the best mappins) in comments
	 * @param generateToString
	 *            true to generate map toString() output of all fields of the b
	 *            type
	 * @param generateToHTML
	 *            true to generate HTML nice report with a lot of colors
	 * @return what you expect
	 */
	public String mapAToB(Class a, Class b, boolean generateUnsetFields, boolean generateToString,
			boolean generateToHTML) {
		String res = "";
		List<L> la = new ArrayList<L>();
		List<L> lb = new ArrayList<L>();
		mapper(a, b, la, lb);
		res += "//creates...\n";
		for (L l : lb) {
			res += l.toJavaCreate();
		}
		res += "\n//sets...\n";
		for (L l : la) {
			res += l.toJavaMapping(lb);
		}
		if (generateUnsetFields) {
			res += "\n//unsets...\n";
			for (L l : lb) {
				res += l.toJavaMissingMappings();
			}
		}
		if (generateToString) {
			res += "\n//toString...\n";
			for (L l : lb) {
				res += l.toString();
			}
		}
		if (generateToHTML) {
			res += "\n\n//toHTML... use http://codebeautify.org/htmlviewer/\n";
			res += "<html><body>";
			for (L l : lb) {
				res += l.toHTML();
			}
			res += "</body></html>";
		}
		return res;
	}

	/**
	 * Maps a to b. Builds 2 types trees as 2 Lists.
	 * 
	 * @param a
	 *            source
	 * @param b
	 *            target
	 * @param la
	 *            tree a (root node)
	 * @param lb
	 *            tree b (root node)
	 */
	public void mapper(Class a, Class b, List<L> la, List<L> lb) {
		makeL(la, a, "Source");
		makeL(lb, b, "Dest");
		lier(la, lb);
	}

	/**
	 * Create a tree.
	 * 
	 * @param a
	 *            type
	 * @param nom
	 *            name of the tree. Must be unique. Allows to generate Java code
	 *            with this name inside variable names!
	 * @return first node
	 */
	public List<L> make(Class a, String nom) {
		List<L> l = new ArrayList<L>();
		makeL(l, a, nom);
		return l;
	}

	/**
	 * Tree maker, recursive. Tell the type, you get a L tree.
	 * 
	 * @param l
	 *            tree
	 * @param a
	 *            type
	 * @param nom
	 *            name of the tree
	 */
	public void makeL(List<L> l, Class a, String nom) {
		Field[] fields = a.getDeclaredFields();
		for (Field field : fields) {
			if (field.getName() != "serialVersionUID") {
				boolean isXmlType = isXmlType(field.getType().getDeclaredAnnotations());
				boolean r = isRequired(field.getDeclaredAnnotations());
				if (isXmlType) {
					// ce champ est d'un des types a recuperer!
					L l1 = new L(field.getName(), r, field.getType(), null, null, getNomInstance(field.getName(), nom));
					List<L> l2 = new ArrayList<L>();
					l1.l = l2;
					l.add(l1);
					makeL(l2, field.getType(), nom);
					for (L l22 : l2) {
						l22.parent = l1;
					}
				} else {
					// ce champ est d'un type simple
					Size size = getSize(field.getDeclaredAnnotations());
					Integer min = null;
					Integer max = null;
					if (size != null) {
						min = size.min();
						max = size.max();
					}
					l.add(new L(field.getName(), r, field.getType(), min, max, getNomInstance(field.getName(), nom)));
				}
			}
		}
	}

	public static boolean isRequired(Annotation[] annot) {
		for (Annotation d : annot) {
			if (d.annotationType().getName().equals(XmlElement.class.getName())) {
				return ((XmlElement) d).required();
			}
		}
		return false;
	}

	public static Size getSize(Annotation[] annot) {
		for (Annotation d : annot) {
			if (d.annotationType().getName().equals(Size.class.getName())) {
				return (Size) d;
			}
		}
		return null;
	}

	public boolean isXmlType(Annotation[] annot) {
		for (Annotation d : annot) {
			if (d.annotationType().getName().equals(XmlType.class.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Links a tree to another. The core of this crazy program!
	 * 
	 * @param la
	 *            tree A
	 * @param lb
	 *            tree B
	 */
	public void lier(List<L> la, List<L> lb) {
		for (L laa : la) {
			for (L lbb : lb) {
				lbb.lier(laa);
			}
			if (laa.l != null)
				lier(laa.l, lb);
		}
	}

	/**
	 * Generates a unique string based on a prefix and a suffix. Allows to
	 * generate unique variable names based on convention: prefixNSuffix, where
	 * N is an integer.
	 * 
	 * @param prefixe
	 *            prefix
	 * @param suffixe
	 *            suffix
	 * @return wath you would like to expect
	 */
	public static String getNomInstance(String prefixe, String suffixe) {
		Integer index = mapIndexByName.get(prefixe + suffixe);
		if (index == null) {
			index = 0;
		}
		index += 1;
		mapIndexByName.put(prefixe + suffixe, index);
		return prefixe + index.intValue() + suffixe;
	}
}
