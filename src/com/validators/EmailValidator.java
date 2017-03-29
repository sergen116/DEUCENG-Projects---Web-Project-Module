package com.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("com.validators.EmailValidator")
public class EmailValidator implements Validator{
	
	private Pattern pattern;
	private Matcher matcher;
	
	//allowing @cs.deu.edu.tr OR @ceng.deu.edu.tr
	private static final String EMAIL_PATTERN =
		"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
		+ "([A-Za-z0-9-]{2,4})(\\.[A-Za-z]{3})(\\.[A-Za-z]{3})(\\.[A-Za-z]{2})$";

	public EmailValidator() {
		pattern = Pattern.compile(EMAIL_PATTERN);
	}

	/**
	 * Validate hex with regular expression
	 *
	 * @param hex
	 *            hex for validation
	 * @return true valid hex, false invalid hex
	 */
	
	public void validate(FacesContext context, UIComponent component,
				Object value) throws ValidatorException {
			// TODO Auto-generated method stub

			matcher = pattern.matcher(value.toString());
			if (!matcher.matches()) {
				FacesMessage msg = new FacesMessage("Email : Invalid format ! Example : sergen.ilter@ceng.deu.edu.tr");
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ValidatorException(msg);
			}

	}
}
