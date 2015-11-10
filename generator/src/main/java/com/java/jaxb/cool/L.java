package com.java.jaxb.cool;

import java.util.List;

/**
 * A "L" is a representation of a field in a type. With a lot of tools.
 * 
 * @author GillesOFraisse
 */
public class L {
	/*
	 * super double jakpot! Bewtween 0 and 1, 0 = bad assomptions for
	 * assignments, 1 = no risks (only sure assomptions). At your own risks !
	 */
	double INDEX_RESSEMBLANCE = 0.6;
	/*
	 * Default generated name for the parent field name if it doesn't exist, for
	 * source type.
	 */
	public final static String SOURCE = "source";
	/*
	 * Default generated name for the parent field name if it doesn't exist, for
	 * target type.
	 */
	public final static String CIBLE = "cible";
	/* field value cannot be empty */
	public boolean required;
	public Class type;
	/* min of something, basically, min length when type is String */
	public Integer min;
	/* see min */
	public Integer max;
	/* field name */
	public String nom;
	/* field parent */
	public L parent = null;
	/*
	 * field instance name in generated java code, pre-computed by the Generator
	 */
	public String instanceName;
	/* list of other L whe this is a root himself */
	public List<L> l = null;
	/*
	 * points to the field of another type when mapping has been done by the
	 * Generator
	 */
	public L mapsTo = null;
	/* represents the "quality" of the generated code, between 0 and 1 */
	public Double indice;

	/**
	 * Constructor.
	 * 
	 * @param nom
	 *            field name
	 * @param required
	 *            nullable
	 * @param type
	 *            Java type
	 * @param min
	 *            min of something, basically, min length when type is String
	 * @param max
	 *            max of something, basically, min length when type is String
	 * @param instanceName
	 *            field instance name in generated java code
	 */
	public L(String nom, boolean required, Class type, Integer min, Integer max, String instanceName) {
		super();
		this.required = required;
		this.type = type;
		this.min = min;
		this.max = max;
		this.nom = nom;
		this.instanceName = instanceName;
	}

	@Override
	public String toString() {
		String str = "";
		Integer indiceInt = 0;
		if (indice != null) {
			indiceInt = (int) (indice * 100);
		}
		str += getToStringMetadata(this, true) + " <=> ";
		if (mapsTo != null) {
			str += getToStringMetadata(mapsTo, true) + " ( " + indiceInt + "% )\n";
		} else {
			str += "?";
		}
		if (l != null) {
			for (L ll : l) {
				str += " " + ll.toString();
			}
		}
		return str;
	}

	/**
	 * Get the nice HTML code that shows the quality of the mapping with colors
	 * (green to red, and black).
	 * 
	 * @return String
	 */
	public String toHTML() {
		String str = "";
		Integer indiceInt = 0;
		if (indice != null) {
			indiceInt = (int) (indice * 100);
		}
		int couleur1 = 0;
		int couleur2 = 0;
		if (indice != null) {
			couleur1 = (int) (255 - ((indice - INDEX_RESSEMBLANCE) / (1 - INDEX_RESSEMBLANCE) * 255));
			couleur2 = (int) (((indice - INDEX_RESSEMBLANCE) / (1 - INDEX_RESSEMBLANCE)) * 200);
		}
		str += "<label style='color:rgb(" + couleur1 + ", " + couleur2 + ", 0);'>";
		if (l != null) {
			str += "<b>";
		}
		str += getToStringMetadata(this, false) + " <=> ";
		if (mapsTo != null) {
			str += getToStringMetadata(mapsTo, true) + " ( " + indiceInt + "% )";
		} else {
			str += "?";
		}
		if (l != null) {
			str += "</b>";
		}
		str += "</label></br>";
		if (l != null) {
			for (L ll : l) {
				str += "<div style='margin-left: 5em;'>" + ll.toHTML() + "</div>";
			}
		}
		return str;
	}

	/**
	 * Get a part of a L as a string.
	 * 
	 * @param ll
	 *            L
	 * @param showParent
	 *            true to print "parent." before the variable name
	 * @return java code
	 */
	public String getToStringMetadata(L ll, boolean showParent) {
		String str = "";
		if (ll.parent != null && showParent) {
			str += ll.parent.nom + ".";
		}
		str += ll.nom + "[ " + ll.type.getSimpleName();
		if (ll.required) {
			str += ", MANDATORY";
		}
		if (ll.min != null || ll.max != null) {
			if (ll.min == null) {
				str += ", maxLength=" + ll.max;
			} else if (max == null) {
				str += ", minLength=" + ll.min;
			}
			if (ll.min != null && ll.max != null) {
				str += ", minLength=" + ll.min + ", maxLength=" + ll.max;
			}
		}
		str += " ]";
		return str;
	}

	/**
	 * Create all the objects used to create only the Java tree code. (a = new
	 * B()... creation tree.
	 * 
	 * @return java code
	 */
	public String toJavaCreate() {
		String str = "";
		// str += "//" + nom + "\n";
		if (l != null) {
			String parentInstanceName = CIBLE;
			if (parent != null) {
				parentInstanceName = parent.instanceName;
				// this est un type contenu par le noeud root de la cible
			}
			// str = "//" + nom + " est parent!\n";
			str += type.getSimpleName() + " " + instanceName + " = new " + type.getSimpleName() + "();\n";
			str += parentInstanceName + ".set" + nom.substring(0, 1).toUpperCase() + nom.substring(1) + "( "
					+ instanceName + " );\n";
			// pas sur de ce if:
			if (mapsTo == null || mapsTo.type != this.type) {
				for (L ll : l) {
					str += ll.toJavaCreate();
				}
			}
		}
		return str;
	}

	/**
	 * Prints in a string the unmapped fields of this tree in comments.
	 * 
	 * @return java code
	 */
	public String toJavaMissingMappings() {
		String str = "";
		if (mapsTo == null && l == null) {
			String parentInstanceName = CIBLE;
			if (parent != null) {
				parentInstanceName = parent.instanceName;
			}
			// rien a mapper, on commente
			str += "//" + parentInstanceName + ".set" + nom.substring(0, 1).toUpperCase() + nom.substring(1) + "( "
					+ getCodeCreateDefaultValue() + " );\n";
		}
		if (l != null && mapsTo == null) {
			for (L ll : l) {
				str += ll.toJavaMissingMappings();
			}
		}
		return str;
	}

	/**
	 * Get the generated default value for an unmapped L. ie the code inside
	 * a.set().
	 * 
	 * @return java code
	 */
	public String getCodeCreateDefaultValue() {
		String res = "";
		if (type == String.class) {
			String generatedString = "";
			if (required) {
				generatedString += "MANDATORY ";
			}
			if (min != null) {
				generatedString += "min: " + min.intValue();
			}
			if (max != null) {
				generatedString += " max: " + max.intValue();
			}
			if (generatedString.length() == 0) {
				generatedString = "free length";
			}
			res += "\"" + generatedString + "\"";

		} else {
			res += "new " + type.getSimpleName() + "()";
		}
		return res;
	}

	/**
	 * Recursive method to determine if this field can be mapped to another.
	 * 
	 * @param lb
	 *            another field
	 */
	public void lier(L lb) {
		// on regarde si ca peut matcher ou si ca matche mieux!
		String nomCompletThis = "";
		String nomCompletLb = "";
		if (parent != null) {
			nomCompletThis += parent.nom + ".";
		}
		nomCompletThis += nom;
		if (lb.parent != null) {
			nomCompletLb += lb.parent.nom + ".";

		}
		nomCompletLb += lb.nom;
		double i = StringSimilarity.similarity(nomCompletLb, nomCompletThis);
		if (l != null && type != lb.type) {
			i = i * 0.2;
		} else if (l != null && type == lb.type) {
			i = i * 2;
		} else if (l == null) {
			if (min != null || max != null) {
				if (min != null && min.equals(lb.min)) {
					i = i * 1.2;
				}
				if (max != null && max.equals(lb.max)) {
					i = i * 1.2;
				}
				if (min != null && max != null && min.equals(lb.min) && max.equals(lb.max)) {
					i = i * 1.2;
				}
			}
			if (type != lb.type) {
				i = i * 0.8;
			}
			if (required != lb.required) {
				i = i * 0.8;
			}
		}
		if (mapsTo != null) {
			if (i < indice) {
				i = 0;
			}
		}
		if (i > INDEX_RESSEMBLANCE) {
			mapsTo = lb;
			if (i > 1) {
				indice = 1.0;
			} else {
				indice = i;
			}
		}
		if (mapsTo == null && l != null)
			for (L ll : l) {
				ll.lier(lb);
			}
	}

	/**
	 * Get the set(X) java code for mapped fields.
	 * 
	 * @param lb
	 *            not null
	 * @return java code
	 */
	public String toJavaSet(L lb) {
		String str = "";
		if (mapsTo == lb) {
			// qq chose � mapper
			String parentInstanceName = CIBLE;
			if (parent != null) {
				parentInstanceName = parent.instanceName;
			}
			// b.setXx(format( a.getYy ))
			if (required && !mapsTo.required) {
				str += "// MANDATORY field whereas source: " + mapsTo.nom + " can be null, please test value !\n";
			}
			if (type != String.class && type != Boolean.class) {
				str += "// pay attention here, the mapping is possibly wrong because type is not String nor boolean !\n";
			}
			str += parentInstanceName + ".set" + nom.substring(0, 1).toUpperCase() + nom.substring(1) + "( ";
			str += getFormatValueFromLinked() + ");\n";

		}
		if (l != null) {
			for (L ll : l) {
				str += ll.toJavaSet(lb);
			}
		}
		return str;
	}

	/**
	 * Get what to put inside a.setB()/works for booleans, for mapped fields.
	 * 
	 * @return java code
	 */
	public String getFormatValueFromLinked() {
		String parentMapsToInstanceName = SOURCE;
		if (mapsTo.parent != null) {
			parentMapsToInstanceName = mapsTo.parent.instanceName;
		}
		String field = parentMapsToInstanceName;
		if (mapsTo.type != Boolean.class) {
			field += ".get";
		} else {
			field += ".is";
		}
		field += mapsTo.nom.substring(0, 1).toUpperCase() + mapsTo.nom.substring(1) + "()";

		String str = field;
		if (type == String.class && mapsTo.type == type) {
			int minL = 0;
			int maxL = 0;
			int minLL = 0;
			int maxLL = 0;
			if (min != null) {
				minL = min.intValue();
			}
			if (mapsTo.min != null) {
				minLL = mapsTo.min.intValue();
			}
			if (max != null) {
				maxL = max.intValue();
			}
			if (mapsTo.max != null) {
				maxLL = mapsTo.max.intValue();
			}
			if (minLL < minL || maxLL > maxL) {
				str = "formatString ( " + field + ", " + minL + ", " + maxL + " )";
			}
		} else if (type == String.class && mapsTo.type == Integer.class) {
			str = "String.valueOf ( " + field + " )";
		} else if (type == Integer.class && mapsTo.type == String.class) {
			str = "Integer.parseInt ( " + field + " )";
		} else if (type == mapsTo.type) {
			str = field;
		} else {
			str = "formatMe ( " + field + " )";
		}
		return str;
	}

	/**
	 * Get the entire java mapping code, but without the creation tree.
	 * 
	 * @param lb
	 *            tree
	 * @return java code
	 */
	public String toJavaMapping(List<L> lb) {
		String str = "";
		if (l != null) {
			String parentMapsToInstanceName = SOURCE;
			if (parent != null) {
				parentMapsToInstanceName = parent.instanceName;
			}
			// A.a a= ab.getXx()
			str += type.getSimpleName() + " " + instanceName + " = " + parentMapsToInstanceName + ".get"
					+ nom.substring(0, 1).toUpperCase() + nom.substring(1) + "();\n";

			if (!required) {
				str += "if ( " + instanceName + " != null ){\n";
			}
			for (L ll : l) {
				str += ll.toJavaMapping(lb);
			}
			if (!required) {
				str += "}\n";
			}

		}
		for (L ll : lb) {
			if (ll.l != null && ll.mapsTo == this && ll.type == this.type) {
				str += ll.instanceName + " = " + instanceName + ";\n";
			} else {
				str += ll.toJavaSet(this);
			}
		}
		return str;
	}
}
