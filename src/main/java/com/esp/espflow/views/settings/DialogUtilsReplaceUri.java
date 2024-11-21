package com.esp.espflow.views.settings;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

/**
 * @author rub'n
 */
@Log4j2
public final class DialogUtilsReplaceUri {

    private static final String BACK_SLASH = "/";
    public static final DialogUtilsReplaceUri INSTANCE = new DialogUtilsReplaceUri();

    private DialogUtilsReplaceUri() {
    }

    /**
     * <p>This method is invoked on each item of the dialog menu, to reconstruct the uri with its fragment.</p>
     *
     * <p><strong>concatFragment</strong> The fragments are concatenated in case they are not, and if they are, we simply replace the previous fragment by this one. </p>
     *
     * @param path           if empty, it comes with /
     * @param ref            can start with settings, settings/fragment
     * @param concatFragment the fragment to be concatenated
     * @return A {@link String} with the parsed fragment
     */
    public String replaceOrConcatFragment(String path, String ref, String concatFragment) {

        String refResult = ref.contains(concatFragment)
                ? ref
                : this.replaceIfNoContains(ref, concatFragment);

        return path.concat("#")
                .concat(refResult);
    }

    /**
     * @param ref      initial fragment
     * @param contains a fragment
     * @return A {@link String} with the parsed fragment
     */
    private String replaceIfNoContains(String ref, String contains) {
        String lastPart = "";
        String result = "";
        if (ref.contains(BACK_SLASH)) {
            lastPart = ref.split(BACK_SLASH)[1];
            result = ref.replace(lastPart, contains);
        } else {
            result = ref.concat(BACK_SLASH).concat(contains);
        }
        return result;
    }

    /**
     * The uri fragment is parsed in order to build in runtime the layout content necessary to apply a good deeplink.
     *
     * @param ref wwith fragment
     * @return A {@link String} with the parsed fragment
     */
    public String parseUriToCreateTheContentForm(String ref) {
        ref = StringUtils.defaultString(ref);
        if (ref.contains(BACK_SLASH) && ref.contains("#")) {
            final String refResult = ref.replace("/#","");
            return refResult.split(BACK_SLASH)[1];
        } else if(ref.contains(BACK_SLASH)){
            return ref.split(BACK_SLASH)[1];
        }
        return ref;
    }

}
