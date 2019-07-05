<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
    <#if section = "title">
        ${msg("updatePhoneNumberTitle", realm.name)}
    <#elseif section = "header">
        ${msg("updatePhoneNumberTitle", realm.name)}
    <#elseif section = "form">
        <#if message?has_content && message.type = "error">
            <#assign errorClass = "govuk-form-group--error" >
        </#if>
        <div id="kc-form" <#if realm.password && social.providers??>class="${properties.kcContentWrapperClass!}"</#if>>
            <div id="kc-form-wrapper" <#if realm.password && social.providers??>class="${properties.kcFormSocialAccountContentClass!} ${properties.kcFormSocialAccountClass!}"</#if>>
                <#if realm.password>
                    <form id="kc-form-login" onsubmit="login.disabled = true; return true;" action="${url.loginAction}" method="post">
                        <div class="${properties.kcFormGroupClass!}">
                            <label for="mobileNumber" class="${properties.kcLabelClass!}">${msg("phoneNumber")}</label>
                            <input tabindex="1" id="mobileNumber" class="${properties.kcInputClass!}" name="mobile_number" type="tel" value="${(phoneNumber!'')}" autocomplete="mobile tel" aria-describedby="mobileNumber-hint" />
                        </div>

                        <div id="kc-form-buttons" class="${properties.kcFormGroupClass!}">
                            <input tabindex="4" class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}" name="login" id="kc-login" type="submit" value="${msg("doLogIn")}"/>
                        </div>
                    </form>
                </#if>
            </div>
        </div>
        <#--
            Hack-alert: Keycloak doesn't provide per-field error messages here,
            so we check global message for need to display validation error styling
        -->
<!--        <div class="govuk-grid-row">
            <div class="govuk-grid-column-full">
                <p>${msg("updatePhoneNumberMessage")}</p>
            </div>
            <form id="kc-totp-login-form" class="${properties.kcFormClass!} govuk-grid-column-two-thirds" action="${url.loginAction}" method="post">
                <div class="govuk-form-group ${errorClass!""}">
                    <div class="${properties.kcFormGroupClass!}">
                        <label for="mobileNumber" class="${properties.kcLabelClass!}">${msg("phoneNumber")}</label>
                        <input tabindex="1" id="mobileNumber" class="${properties.kcInputClass!}" name="mobile_number" type="tel" value="${(phoneNumber!'')}" autocomplete="mobile tel" aria-describedby="mobileNumber-hint" />
                    </div>
                </div>

                <div class="govuk-form-group">
                    <input tabindex="4" class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}" name="login" id="kc-login" type="submit" value="${msg("doLogIn")}"/>
                </div>
            </form>
        </div> -->
    </#if>
</@layout.registrationLayout>
