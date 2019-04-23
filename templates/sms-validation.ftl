<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
    <#if section = "title" || section = "header">
        ${msg("sms-auth.title")}
    <#elseif section = "form">
        <#--
            Hack-alert: Keycloak doesn't provide per-field error messages here,
            so we check global message for need to display validation error styling
        -->
        <#if message?has_content && message.type = "error">
            <#assign errorClass = "govuk-form-group--error" >
        </#if>
        <div id="kc-form" <#if realm.password && social.providers??>class="${properties.kcContentWrapperClass!}"</#if>>
            <div id="kc-form-wrapper" <#if realm.password && social.providers??>class="${properties.kcFormSocialAccountContentClass!} ${properties.kcFormSocialAccountClass!}"</#if>>
                <div class="">
                    <p>${msg("sms-auth.instruction")}</p>
                </div>
                <form id="kc-login-form" class="${properties.kcFormClass!} govuk-grid-column-two-thirds" action="${url.loginAction}" method="post">
                    <div class="${properties.kcFormGroupClass!} ${errorClass!}">
                        <label for="smsCode" class="${properties.kcLabelClass!}">${msg("sms-auth.code")}</label>
                        <input tabindex="1" id="smsCode" class="${properties.kcInputClass!}" name="smsCode" autocomplete="false" />
                    </div>
                    <div id="kc-form-buttons" class="${properties.kcFormGroupClass!}">
                        <input tabindex="4" class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}" name="login" id="kc-login" type="submit" value="${msg("doSubmit")}"/>
                    </div>
                </form>
            </div>
        </div>
    </#if>
</@layout.registrationLayout>
