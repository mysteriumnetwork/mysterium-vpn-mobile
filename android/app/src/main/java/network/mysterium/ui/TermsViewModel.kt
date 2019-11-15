/*
 * Copyright (C) 2019 The "mysteriumnetwork/mysterium-vpn-mobile" Authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package network.mysterium.ui

import androidx.lifecycle.ViewModel
import network.mysterium.db.AppDatabase
import network.mysterium.db.Terms

class TermsViewModel(private val appDatabase: AppDatabase): ViewModel() {
    private val termsVersion = "1"

    suspend fun acceptCurrentTerms() {
        appDatabase.termsDao().delete()
        appDatabase.termsDao().insert(Terms(termsVersion))
    }

    suspend fun checkTermsAccepted(): Boolean {
        val terms = appDatabase.termsDao().get() ?: return false
        return terms.version == termsVersion
    }

    val termsText = """
        <h3>Terms & Conditions of MysteriumVPN for Users</h3>

The following Terms and Conditions (“T&C”) govern the use of the Mysterium open source software platform (“Mysterium Platform”) and MysteriumVPN Software (the “Software”). Prior to any use of the Mysterium Platform, the User confirms to understand and expressly agrees to all of the Terms.

MysteriumVPN is an Open Source software that enables you (the “Client”) to use the internet connection and resources of our contributors (the “Nodes”) for your private browsing using Mysterium Platform.

The Mysterium Platform rests on open-source software. This is an experimental version of the Mysterium Platform, and there is a risk that the NetSys Inc. (“Network Operator”) or other third parties not directly affiliated with the Network Operator, might unintentionally introduce weaknesses or bugs into the core infrastructural elements of the Mysterium Platform or Software causing the system to lose cryptocurrency stored in one or more Client accounts or other accounts or lose sums of other valued tokens issued on the Mysterium Platform, or causing to suffer other losses, inconvenience, or damage to its users. The user expressly knows and agrees that the user is using the Mysterium Platform and MysteriumVPN software at the user’s sole risk.

These terms and conditions constitute an agreement between you and Network Operator and governs your access and use of MysteriumVPN software and private browsing services provided by Mysterium Platform (the “Services”).

The Network Operator or other creators of the Mysterium Platform are entitled to discontinue this project at any time without any further explanation or prior notification at this experimental stage.


PLEASE READ THESE T&C CAREFULLY IF YOU WISH TO USE THE SERVICES. IF YOU DO NOT AGREE TO BE BOUND BY THESE T&C, PLEASE DO NOT USE THE SERVICES.

<h4>Your Use of the Services</h4>

The Services are provided to you free of charge. You may not use the Services if you are under the age of 18 or if you are not the owner of the device on which you install the Software or otherwise use the Services.

By using the services of Network Operator, you understand and agree that the Services are internet security and private browsing services, and this mechanism is not a service to be used for criminal and/or other illegal acts. By using the Services, you accept not to violate any law of any jurisdiction that you are originating from or residing at. It is your responsibility to know and comprehend any and all relevant laws related to any jurisdiction or venue that concerns you and your actions.


<h4>Client’s Account</h4>

Before starting to use the Services, you must download Mysterium software and create an account entering your credentials and private/public keys. You hereby agree to provide true, accurate, current and complete information as may be prompted by any registration forms regarding your registration or use of Services.

By creating an account and starting to use the Services you agree and accept unconditionally that:

you cannot provide your credentials and private/public keys to others;
you will not share your credentials and private/public keys publicly.

<h4>Prohibited Conduct</h4>

You may not use the Services in any manner that could damage, disable, overburden, or impair the servers and other resources of Network Operator and the Nodes, or interfere with any third party’s use of the Services. You may not attempt to gain unauthorized access to any aspect of the Services or to information for which you have not been granted access.

By using the Services, you commit not to carry out any of these criminal and other illegal actions in or through the Services using our resources and/or resources of the Nodes:

extortion, blackmail, kidnapping, rape, murder, sale/purchase of stolen credit cards, sale/purchase of stolen sale/purchase, sale/purchase of illegal sale/purchase, performance of identity theft;
use of stolen credit cards, credit card fraud, wire fraud,
hacking, pharming, phishing, or spamming of any form, web scraping through our Service in any form or scale;
exploitation of or contribution to children exploitation photographically, digitally or in any other way;
port scanning, sending opt-in email, scanning for open relays or open proxies, sending unsolicited e-mail or any version or type of email sent in vast quantities even if the email is lastly sent off through another server;
assaulting in any way or form any other network or computer while using the Service;
any other activities that are against the law of the country you originate from or reside in, and/or any other activities that are not compatible with the principles of democracy, freedom of speech, freedom of expression, and human rights.

<h4>Suspension or Termination of the Account</h4>

Network Operator does not have an obligation to monitor the activities on the Mysterium Platform, and he does not carry out such monitoring actively.

In the case Network Operator notices accidently or is notified of any suspicious activities using your Account that may result in violation of these T&C (see section “Prohibited Conduct” for more detail), in order to maintain the integrity of the Mysterium Platform Network Operator reserves the right (but not an obligation) to carry out any actions he considers necessary in the particular situation, including, but not limited to, the suspension and/or the termination of your Account immediately without any previous notification.

Network Operator can terminate any Client account for the violation of these T&C immediately without notice or suspend the account until further clarification, investigation, or communication with the Client.

<h4>Limitation of Liability</h4>

NEITHER THE NETWORK OPERATOR, NOR THE NODES WILL BE LIABLE IN ANY WAY OR FORM FOR ACTIONS DONE BY THE CLIENTS AND/OR ANY ACTIONS DONE USING CLIENT’S ACCOUNT INCLUDING CRIMINAL LIABILITY AND CIVIL LIABILITY FOR ANY HARM. SOLELY THE CLIENT IS LIABLE FOR ALL HIS ACTIONS USING THE SERVICES, THE SOFTWARE AND THE RESOURCES OF NETWORK OPERATOR AND OF THE RESOURCES THE NODES.

NETWORK OPERATOR, ITS OWNERS, EMPLOYEES, AGENTS AND OTHERS THAT ARE INVOLVED WITH THE NETWORK OPERATOR ARE NOT IN ANY WAY OR FORM LIABLE FOR ANY HARM OF ANY SORT EXECUTED OR NOT EXECUTED, RESULTING FROM OR ARISING THROUGH OR FROM THE USE OF ANY ACCOUNT REGISTERED USING MYSTERIUM.

IN NO EVENT WILL WE OR ANY OTHER PARTY WHO HAS BEEN INVOLVED IN THE CREATION, PRODUCTION, DISTRIBUTION, PROMOTION, OR MARKETING OF SOFTWARE AND/OR THE SERVICES BE LIABLE TO YOU OR ANY OTHER PARTY FOR ANY SPECIAL, INDIRECT, INCIDENTAL, RELIANCE, EXEMPLARY, OR CONSEQUENTIAL DAMAGES, INCLUDING, WITHOUT LIMITATION, LOSS OF DATA OR PROFITS, OR FOR INABILITY TO USE THE SERVICE, EVEN IF WE OR SUCH OTHER PARTY HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. SUCH LIMITATION SHALL APPLY NOTWITHSTANDING ANY FAILURE OF ESSENTIAL PURPOSE OF ANY LIMITED REMEDY AND TO THE FULLEST EXTENT PERMITTED BY LAW. IN NO EVENT, SHALL OUR AGGREGATE LIABILITY TO YOU AND ANY OTHER PARTY, WHETHER DIRECT OR INDIRECT, EXCEED ONE HUNDRED DOLLARS (${'$'}100.00) FOR ANY AND ALL CLAIMS, DAMAGES, AND OTHER THEORY OF LIABILITY.

<h4>No-logging and Traceability of Your Activities</h4>

Network Operator guarantees a strict no-logs policy of the Services, meaning that your activities using the Services are not actively monitored, recorded, logged, stored or passed to any third party by the Network Operator.

Network Operator does not store connection time stamps, used bandwidth, traffic logs nor IP addresses. Network Operator does not provide any information about their Clients and the fact of the use of Services by a particular Client to any third party.

In the case Client’s use of Services constitute a breach of these T&C or respective legal acts and this causes legal risks for the Nodes whose resources Client was using, Network Operator might be obliged to provide the information related to such particular use of Services by the officially binding order of law enforcement authorities or other governmental agencies. As Network Operator does not trace the activities of their Clients, Network Operator would not be technically able to provide the authorities with more detailed information about their particular Clients activities. However, as Network Operator has no intention to contribute to any illegal and/or unauthorized activities, and/or to involve the Nodes in any of such, the Network Operator can cooperate with the authorities on his own account.

Using the Services you agree and accept that Network Operator is free to take the decisions to provide any help and assistance including legal advice that Network Operator might find necessary or suitable to the Nodes who might need it in relation with a possible or an ongoing official investigation related to the use of their resources by the Clients.

By using the Services you also agree and understand that your activities using the Services in some cases can be traceable by the Nodes and their internet service providers.

In order to maintain the Services and to ensure the proper functioning of Mysterium, the Network Operator does collect limited and anonymized personal user information and site performance data (see section “Privacy Policy” for more details).

<h4>Data Privacy</h4>

Network Operator is committed to your privacy and does not collect, log, organize, structure, store, use, disseminate or make otherwise available any personal data of the Clients, e.g. Client’s identity, IP address, browsing history, traffic destination, data content, DNS queries from the Clients connected to Mysterium Network, or any other personal information which could be considered personal data (i.e. any information relating to an identified or identifiable natural person).
Network Operator will collect anonymous statistics to improve the performance of Mysterium Network. This information is fully anonymized and cannot be tied back to any individual users.

</h5>Limited License</h4>

During your use of Services, we hereby grant you a personal, non-exclusive, non-transferable, non-assignable, non-sub-licensable, revocable and limited license to access and use the Services and to install a copy of the Software on your personal device.

You fully understand and accept that the Software is licensed to you, not sold.

Permission is granted to anyone to use this Software for any purpose, including commercial applications, and to alter it freely (without the right of redistribution), subject to the following restrictions:

the origin of this Software must not be misrepresented; you must not claim that you wrote the original Software. If you use this Software in a product, an acknowledgment in the product documentation would be appreciated but is not mandatory;
altered versions must be plainly marked as such, and must not be misrepresented as being the original Software or any other creation of Network Operator, or the product created under any commission of Mysterium Platform;
this notice may not be removed or altered from any derivative works based on the Software.

In any case, Network Operator does not bear any liability for any actions based on or related to the derivative works based on the Software. The users of such derivative works use them on their own account.

Except as expressly indicated in these T&C or within any of the Services you may not: (i) sell, lend, rent, assign, export, sublicense or otherwise transfer the Software or the Services; (ii) alter, delete or conceal any copyright, trademark or other notices in connection with the Services or the Software; (iii) interfere with or impair the use of others of the Services or with any network connected to the Services; (iv) use the Services or the Software by yourself or in conjunction with any other products to infringe upon any third party's rights, including without limitation third party's intellectual property rights, to invade third party’s privacy in any way, or to track, store, transmit or record personal information about any other user of the Services or the Software; (v) otherwise violate applicable laws including without limitation copyright and trademark laws and applicable communications regulations and statutes.

Any such forbidden uses shall immediately and automatically terminate your license to use the Software and the Services, without derogating from any other remedies available to us at law or in equity.

</h5>Intellectual Property Rights</h4>

The Software, including any versions, revisions, corrections, modifications, enhancements and/or upgrades thereto, accompanying materials, services and any copies you are permitted to make under these T&C are owned by us or our licensors and are protected under intellectual property laws, including copyright laws and treaties. You agree, accept and acknowledge that all right, title, and interest in and to the Software and associated intellectual property rights (including, without limitation, any patents (registered or pending), copyrights, trade secrets, designs or trademarks), evidenced by or embodied in or attached or connected or related to the Software are and shall remain owned solely by us or our licensors. These T&C do not convey to you any interest in or to the Software or any of the Services, but only a limited, revocable right of use in accordance with the terms of these T&C. Nothing in these T&C constitutes a waiver of our intellectual property rights under any law.

Network Operator and Mysterium logos and trademarks owned by us or our licensors, and no right, license, or interest in any such trademarks is granted hereunder. We respect the intellectual property of others, and we ask you to do the same. It is important (and a condition of these T&C) that you comply with all copyright laws and other provisions in connection with any content agreement to which you may be a party through the Services.

<h4>Warranty Disclaimers</h4>

THE SOFTWARE AND THE SERVICES ARE PROVIDED ON AN "AS IS" AND "AS AVAILABLE" BASIS,
WITHOUT ANY WARRANTY OF ANY KIND (INCLUDING SUPPORT OR OTHER SERVICES BY US OR OUR LICENSORES). YOU AGREE THAT YOUR USE OF THE SERVICES AND SOFTWARE SHALL BE AT YOUR SOLE RISK AND RESPONSIBILITY. TO THE FULLEST EXTENT PERMITTED BY APPLICABLE LAW, WE, OUR LICENSORS, OFFICERS, DIRECTORS, EMPLOYEES, AND AGENTS DISCLAIM ALL WARRANTIES, EXPRESS OR IMPLIED INCLUDING, WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND NON INFRINGEMENT. WE DO NOT WARRANT THAT THE SOFTWARE OR SERVICES: (A) WILL BE ERROR OR DEFECT FREE OR OTHERWISE FREE FROM ANY INTERRUPTIONS OR OTHER FAILURES; (B) WILL MEET YOUR REQUIREMENTS; OR (C) THAT ANY ERROR WILL BE IMMEDIATELY FIXED; WE MAKE NO WARRANTIES OR REPRESENTATIONS ABOUT THE ACCURACY OR COMPLETENESS OF THE CONTENT (INCLUDING ANY USER CONTENT) OR TO ANY THIRD PARTY SITES OR APPLICATIONS OR CONTENT OR ANY PORTION OR COMPONENT OF EITHER AND ASSUME NO LIABILITY OR RESPONSIBILITY AND DISCLAIM ALL WARRANTIES FOR ANY (I) PROBLEMS OR AVAILABILITY OF INTERNET CONNECTIONS (II) ERRORS, MISTAKES, OR INACCURACIES IN SOFTWARE OR SERVICES, (III) PROPERTY DAMAGE, OF ANY NATURE WHATSOEVER, RESULTING FROM YOUR ACCESS TO AND USE OF THE SERVICES OR TO ANY THIRD PARTY SITE, (IV) ANY UNAUTHORIZED ACCESS TO YOUR DEVICE OR USE OF OUR SECURE SERVERS OR ANY AND ALL PERSONAL INFORMATION OR FINANCIAL INFORMATION STORED THEREIN, (V) ANY INTERRUPTION OR CESSATION OF TRANSMISSION REGARDING THE SERVICES, (VI) ANY BUGS, VIRUSES, TROJAN HORSES, OR OTHER MALICIOUS CODE WHICH MAY BE TRANSMITTED TO OR THROUGH THE SERVICES BY ANY THIRD PARTY, (VII) ANY LOSS OR DAMAGE OF ANY KIND INCURRED AS A RESULT OF THE USE OF ANY CONTENT POSTED, EMAILED, TRANSMITTED OR OTHERWISE MADE AVAILABLE VIA THE SERVICES.

WE DO NOT WARRANT, ENDORSE, GUARANTEE, OR ASSUME RESPONSIBILITY FOR ANY PRODUCT OR SERVICES ADVERTISED OR OFFERED BY A THIRD PARTY THROUGH THE SERVICES OR ANY HYPERLINKED WEBSITE OR FEATURED IN ANY BANNER OR OTHER ADVERTISING, AND WE WILL NOT BE A PARTY TO ANY TRANSACTION OR OTHER ENGAGEMENT WITH SUCH ADVERTISING OR IN ANY WAY BE RESPONSIBLE FOR MONITORING ANY TRANSACTION BETWEEN YOU AND THIRD-PARTY PROVIDERS OF PRODUCTS OR SERVICES. YOU ASSUME ALL RISK AS TO THE QUALITY, FUNCTION, AND PERFORMANCE OF THE SERVICES, AND TO ALL TRANSACTIONS YOU UNDERTAKE ON THROUGH THE SERVICES.

<h4>Indemnification</h4>

BY USING THE SERVICES YOU FULLY UNDERSTAND AND AGREE THAT IS AN EXPERIMENTAL VERSION OF THE MYSTERIUM PLATFORM, AND THERE IS A RISK THAT THE NETWORK OPERATOR OR OTHER THIRD PARTIES NOT DIRECTLY AFFILIATED WITH THE NETWORK OPERATOR, MAY INTRODUCE WEAKNESSES OR BUGS INTO THE CORE INFRASTRUCTURAL ELEMENTS OF THE MYSTERIUM PLATFORM CAUSING THE SYSTEM TO LOSE CRYPTOCURRENCY STORED IN ONE OR MORE CLIENT ACCOUNTS OR OTHER ACCOUNTS OR LOSE SUMS OF OTHER VALUED TOKENS ISSUED ON THE MYSTERIUM PLATFORM, OR CAUSING TO SUFFER OTHER LOSSES, INCONVENIENCE, OR DAMAGE TO ITS USERS.

You hereby agree to indemnify, defend and hold us, our subsidiaries, parent corporation and affiliates, partners, sponsors and all of their respective officers, directors, owners, employees, agents, attorneys, licensors, representatives, licensees, and suppliers (collectively, "Parties"), harmless from and against any and all liabilities, losses, expenses, damages, and costs (including reasonable attorneys' fees), incurred by any of the Parties in connection with any claim arising out of your use of the Software or Services, any use or alleged use of your account, username, or your password by any person, whether or not authorized by you, your violation or breach of these T&C or your violation of the rights of any other person or entity.

<h4>Term and Termination</h4>

These T&C become effective upon the installation of the Software until terminated by either you or us (the "Term"). You may terminate your relationship with us at any time by completely uninstalling the Software. In the case you fail to comply with these T&C or any other agreement you have concluded with us, this will terminate your Software license and this agreement. Upon termination of this agreement the Software license granted to you shall automatically expire and you shall discontinue all further use of the Software and Services.

We have the right to take any of the following actions in our sole discretion at any time without any prior notice to you: (i) restrict, deactivate, suspend, or terminate your access to the Services, including deleting your accounts and all related information and files contained in your account; (ii) refuse, move, or remove any material that is available on or through the Services; (iii) establish additional general practices and limits concerning use of the Services.

We may take any of the above actions for any reason, as determined by us in our sole discretion, including, but not be limited to, (a) your breach or violation of these T&C, (b) requests by law enforcement authority or other governmental agency, (c) a request by you, (d) discontinuance or material modification to the Services (or any part thereof), and (e) unexpected technical or security issues or problems.

You agree that we will not be liable to you or any third party for taking any of these actions.

<h4>Miscellaneous</h4>

The Software is intended for use only in compliance with applicable laws and you undertake to use it in accordance with all such applicable laws. Without derogating from the foregoing and from any other terms herein, you agree to comply with all applicable export laws and restrictions and regulations and agree that you will not export, or allow the export or re-export of the Software in violation of any such restrictions, laws or regulations.
By logging in to Mysterium and using the Services, you agree to the T&C, including all other policies incorporated by reference.

These T&C and the relationship between you and Network Operator shall be governed by and construed in accordance with law of Panama. You agree that any legal action arising out of or relating to these T&C, Software or your use of, or inability to use, the Services shall be filed exclusively in the competent courts of Panama.

You agree that these T&C and our rights hereunder may be assigned, in whole or in part, by us or our affiliate to any third party, at our sole discretion, including an assignment in connection with a merger, acquisition, reorganization or sale of substantially all of our assets, or otherwise, in whole or in part. You may not delegate, sublicense or assign your rights under these T&C.

That is the whole agreement. Network Operator may rewrite the T&C from time to time. The T&C become binding from the time it is updated on our website. It is your responsibility to check for the new provisions of T&C periodically.

<h4>Contact us:</h4>

For any questions you may contact us: <b>team@netsys.technology</b>

Last update: <b>29-03-2018</b>
    """.trimIndent()
}