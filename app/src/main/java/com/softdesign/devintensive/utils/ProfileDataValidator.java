package com.softdesign.devintensive.utils;

/**
 * Validators for profile data which can be inputted in EditText fields.
 */
public class ProfileDataValidator implements TextValidator {

    private int mValidatorType;

    /** The validator for phone numbers. */
    public static final int PHONE_NUMBER_VALIDATOR = 0;


    public ProfileDataValidator(int validatorType) {
        this.mValidatorType = validatorType;
    }


    public int getValidatorType() {
        return mValidatorType;
    }

    public void setValidatorType(int validatorType) {
        this.mValidatorType = validatorType;
    }

    @Override
    public boolean validate(String text) {
        switch (mValidatorType) {
            case PHONE_NUMBER_VALIDATOR:
                return validatePhoneNumber(text);

            // TODO: others types
        }
        return false;
    }

    protected boolean validatePhoneNumber(String phone) {
        if (phone.length() < 11 || phone.length() > 20) return false;

        // TODO: number checking

        return true;
    }

}
