/**
 *	This generated bean class EjbJar matches the schema element 'ejb-jar'.
 *
 *	===============================================================
 *	
 *	                This group keeps the usage of the contained JNDI environment
 *	                reference elements consistent across J2EE deployment
 *	                descriptors.
 *	            
 *	===============================================================
 *
 *	This class matches the root element of the XML Schema,
 *	and is the root of the following bean graph:
 *
 *	ejbJar <ejb-jar> : EjbJar
 *		environment <environment> : EnvironmentType[0,1]
 *			moduleId <moduleId> : ArtifactType[0,1]
 *				groupId <groupId> : java.lang.String[0,1]
 *				artifactId <artifactId> : java.lang.String
 *				version <version> : java.lang.String[0,1]
 *				type <type> : java.lang.String[0,1]
 *				groupId2 <groupId> : java.lang.String[0,1]
 *				artifactId2 <artifactId> : java.lang.String
 *				version2 <version> : java.lang.String[0,1]
 *				type2 <type> : java.lang.String[0,1]
 *				groupId3 <groupId> : java.lang.String[0,1]
 *				artifactId3 <artifactId> : java.lang.String
 *				version3 <version> : java.lang.String[0,1]
 *				type3 <type> : java.lang.String[0,1]
 *			dependencies <dependencies> : DependenciesType[0,1]
 *				dependency <dependency> : DependencyType[0,n]
 *					groupId <groupId> : java.lang.String[0,1]
 *					artifactId <artifactId> : java.lang.String
 *					version <version> : java.lang.String[0,1]
 *					type <type> : java.lang.String[0,1]
 *					import <import> : java.lang.String[0,1] 	[enumeration (classes), enumeration (services)]
 *					groupId2 <groupId> : java.lang.String[0,1]
 *					artifactId2 <artifactId> : java.lang.String
 *					version2 <version> : java.lang.String[0,1]
 *					type2 <type> : java.lang.String[0,1]
 *					import2 <import> : java.lang.String[0,1] 	[enumeration (classes), enumeration (services)]
 *					groupId3 <groupId> : java.lang.String[0,1]
 *					artifactId3 <artifactId> : java.lang.String
 *					version3 <version> : java.lang.String[0,1]
 *					type3 <type> : java.lang.String[0,1]
 *					import3 <import> : java.lang.String[0,1] 	[enumeration (classes), enumeration (services)]
 *				dependency2 <dependency> : DependencyType[0,n]
 *					groupId <groupId> : java.lang.String[0,1]
 *					artifactId <artifactId> : java.lang.String
 *					version <version> : java.lang.String[0,1]
 *					type <type> : java.lang.String[0,1]
 *					import <import> : java.lang.String[0,1] 	[enumeration (classes), enumeration (services)]
 *					groupId2 <groupId> : java.lang.String[0,1]
 *					artifactId2 <artifactId> : java.lang.String
 *					version2 <version> : java.lang.String[0,1]
 *					type2 <type> : java.lang.String[0,1]
 *					import2 <import> : java.lang.String[0,1] 	[enumeration (classes), enumeration (services)]
 *					groupId3 <groupId> : java.lang.String[0,1]
 *					artifactId3 <artifactId> : java.lang.String
 *					version3 <version> : java.lang.String[0,1]
 *					type3 <type> : java.lang.String[0,1]
 *					import3 <import> : java.lang.String[0,1] 	[enumeration (classes), enumeration (services)]
 *				dependency3 <dependency> : DependencyType[0,n]
 *					groupId <groupId> : java.lang.String[0,1]
 *					artifactId <artifactId> : java.lang.String
 *					version <version> : java.lang.String[0,1]
 *					type <type> : java.lang.String[0,1]
 *					import <import> : java.lang.String[0,1] 	[enumeration (classes), enumeration (services)]
 *					groupId2 <groupId> : java.lang.String[0,1]
 *					artifactId2 <artifactId> : java.lang.String
 *					version2 <version> : java.lang.String[0,1]
 *					type2 <type> : java.lang.String[0,1]
 *					import2 <import> : java.lang.String[0,1] 	[enumeration (classes), enumeration (services)]
 *					groupId3 <groupId> : java.lang.String[0,1]
 *					artifactId3 <artifactId> : java.lang.String
 *					version3 <version> : java.lang.String[0,1]
 *					type3 <type> : java.lang.String[0,1]
 *					import3 <import> : java.lang.String[0,1] 	[enumeration (classes), enumeration (services)]
 *			hiddenClasses <hidden-classes> : ClassFilterType[0,1]
 *				filter <filter> : java.lang.String[0,n]
 *				filter2 <filter> : java.lang.String[0,n]
 *				filter3 <filter> : java.lang.String[0,n]
 *			nonOverridableClasses <non-overridable-classes> : ClassFilterType[0,1]
 *				filter <filter> : java.lang.String[0,n]
 *				filter2 <filter> : java.lang.String[0,n]
 *				filter3 <filter> : java.lang.String[0,n]
 *			inverseClassloading <inverse-classloading> : EmptyType[0,1]
 *			suppressDefaultEnvironment <suppress-default-environment> : EmptyType[0,1]
 *			moduleId2 <moduleId> : ArtifactType[0,1]
 *				groupId <groupId> : java.lang.String[0,1]
 *				artifactId <artifactId> : java.lang.String
 *				version <version> : java.lang.String[0,1]
 *				type <type> : java.lang.String[0,1]
 *				groupId2 <groupId> : java.lang.String[0,1]
 *				artifactId2 <artifactId> : java.lang.String
 *				version2 <version> : java.lang.String[0,1]
 *				type2 <type> : java.lang.String[0,1]
 *				groupId3 <groupId> : java.lang.String[0,1]
 *				artifactId3 <artifactId> : java.lang.String
 *				version3 <version> : java.lang.String[0,1]
 *				type3 <type> : java.lang.String[0,1]
 *			dependencies2 <dependencies> : DependenciesType[0,1]
 *				dependency <dependency> : DependencyType[0,n]
 *					groupId <groupId> : java.lang.String[0,1]
 *					artifactId <artifactId> : java.lang.String
 *					version <version> : java.lang.String[0,1]
 *					type <type> : java.lang.String[0,1]
 *					import <import> : java.lang.String[0,1] 	[enumeration (classes), enumeration (services)]
 *					groupId2 <groupId> : java.lang.String[0,1]
 *					artifactId2 <artifactId> : java.lang.String
 *					version2 <version> : java.lang.String[0,1]
 *					type2 <type> : java.lang.String[0,1]
 *					import2 <import> : java.lang.String[0,1] 	[enumeration (classes), enumeration (services)]
 *					groupId3 <groupId> : java.lang.String[0,1]
 *					artifactId3 <artifactId> : java.lang.String
 *					version3 <version> : java.lang.String[0,1]
 *					type3 <type> : java.lang.String[0,1]
 *					import3 <import> : java.lang.String[0,1] 	[enumeration (classes), enumeration (services)]
 *				dependency2 <dependency> : DependencyType[0,n]
 *					groupId <groupId> : java.lang.String[0,1]
 *					artifactId <artifactId> : java.lang.String
 *					version <version> : java.lang.String[0,1]
 *					type <type> : java.lang.String[0,1]
 *					import <import> : java.lang.String[0,1] 	[enumeration (classes), enumeration (services)]
 *					groupId2 <groupId> : java.lang.String[0,1]
 *					artifactId2 <artifactId> : java.lang.String
 *					version2 <version> : java.lang.String[0,1]
 *					type2 <type> : java.lang.String[0,1]
 *					import2 <import> : java.lang.String[0,1] 	[enumeration (classes), enumeration (services)]
 *					groupId3 <groupId> : java.lang.String[0,1]
 *					artifactId3 <artifactId> : java.lang.String
 *					version3 <version> : java.lang.String[0,1]
 *					type3 <type> : java.lang.String[0,1]
 *					import3 <import> : java.lang.String[0,1] 	[enumeration (classes), enumeration (services)]
 *				dependency3 <dependency> : DependencyType[0,n]
 *					groupId <groupId> : java.lang.String[0,1]
 *					artifactId <artifactId> : java.lang.String
 *					version <version> : java.lang.String[0,1]
 *					type <type> : java.lang.String[0,1]
 *					import <import> : java.lang.String[0,1] 	[enumeration (classes), enumeration (services)]
 *					groupId2 <groupId> : java.lang.String[0,1]
 *					artifactId2 <artifactId> : java.lang.String
 *					version2 <version> : java.lang.String[0,1]
 *					type2 <type> : java.lang.String[0,1]
 *					import2 <import> : java.lang.String[0,1] 	[enumeration (classes), enumeration (services)]
 *					groupId3 <groupId> : java.lang.String[0,1]
 *					artifactId3 <artifactId> : java.lang.String
 *					version3 <version> : java.lang.String[0,1]
 *					type3 <type> : java.lang.String[0,1]
 *					import3 <import> : java.lang.String[0,1] 	[enumeration (classes), enumeration (services)]
 *			hiddenClasses2 <hidden-classes> : ClassFilterType[0,1]
 *				filter <filter> : java.lang.String[0,n]
 *				filter2 <filter> : java.lang.String[0,n]
 *				filter3 <filter> : java.lang.String[0,n]
 *			nonOverridableClasses2 <non-overridable-classes> : ClassFilterType[0,1]
 *				filter <filter> : java.lang.String[0,n]
 *				filter2 <filter> : java.lang.String[0,n]
 *				filter3 <filter> : java.lang.String[0,n]
 *			inverseClassloading2 <inverse-classloading> : EmptyType[0,1]
 *			suppressDefaultEnvironment2 <suppress-default-environment> : EmptyType[0,1]
 *			moduleId3 <moduleId> : ArtifactType[0,1]
 *				groupId <groupId> : java.lang.String[0,1]
 *				artifactId <artifactId> : java.lang.String
 *				version <version> : java.lang.String[0,1]
 *				type <type> : java.lang.String[0,1]
 *				groupId2 <groupId> : java.lang.String[0,1]
 *				artifactId2 <artifactId> : java.lang.String
 *				version2 <version> : java.lang.String[0,1]
 *				type2 <type> : java.lang.String[0,1]
 *				groupId3 <groupId> : java.lang.String[0,1]
 *				artifactId3 <artifactId> : java.lang.String
 *				version3 <version> : java.lang.String[0,1]
 *				type3 <type> : java.lang.String[0,1]
 *			dependencies3 <dependencies> : DependenciesType[0,1]
 *				dependency <dependency> : DependencyType[0,n]
 *					groupId <groupId> : java.lang.String[0,1]
 *					artifactId <artifactId> : java.lang.String
 *					version <version> : java.lang.String[0,1]
 *					type <type> : java.lang.String[0,1]
 *					import <import> : java.lang.String[0,1] 	[enumeration (classes), enumeration (services)]
 *					groupId2 <groupId> : java.lang.String[0,1]
 *					artifactId2 <artifactId> : java.lang.String
 *					version2 <version> : java.lang.String[0,1]
 *					type2 <type> : java.lang.String[0,1]
 *					import2 <import> : java.lang.String[0,1] 	[enumeration (classes), enumeration (services)]
 *					groupId3 <groupId> : java.lang.String[0,1]
 *					artifactId3 <artifactId> : java.lang.String
 *					version3 <version> : java.lang.String[0,1]
 *					type3 <type> : java.lang.String[0,1]
 *					import3 <import> : java.lang.String[0,1] 	[enumeration (classes), enumeration (services)]
 *				dependency2 <dependency> : DependencyType[0,n]
 *					groupId <groupId> : java.lang.String[0,1]
 *					artifactId <artifactId> : java.lang.String
 *					version <version> : java.lang.String[0,1]
 *					type <type> : java.lang.String[0,1]
 *					import <import> : java.lang.String[0,1] 	[enumeration (classes), enumeration (services)]
 *					groupId2 <groupId> : java.lang.String[0,1]
 *					artifactId2 <artifactId> : java.lang.String
 *					version2 <version> : java.lang.String[0,1]
 *					type2 <type> : java.lang.String[0,1]
 *					import2 <import> : java.lang.String[0,1] 	[enumeration (classes), enumeration (services)]
 *					groupId3 <groupId> : java.lang.String[0,1]
 *					artifactId3 <artifactId> : java.lang.String
 *					version3 <version> : java.lang.String[0,1]
 *					type3 <type> : java.lang.String[0,1]
 *					import3 <import> : java.lang.String[0,1] 	[enumeration (classes), enumeration (services)]
 *				dependency3 <dependency> : DependencyType[0,n]
 *					groupId <groupId> : java.lang.String[0,1]
 *					artifactId <artifactId> : java.lang.String
 *					version <version> : java.lang.String[0,1]
 *					type <type> : java.lang.String[0,1]
 *					import <import> : java.lang.String[0,1] 	[enumeration (classes), enumeration (services)]
 *					groupId2 <groupId> : java.lang.String[0,1]
 *					artifactId2 <artifactId> : java.lang.String
 *					version2 <version> : java.lang.String[0,1]
 *					type2 <type> : java.lang.String[0,1]
 *					import2 <import> : java.lang.String[0,1] 	[enumeration (classes), enumeration (services)]
 *					groupId3 <groupId> : java.lang.String[0,1]
 *					artifactId3 <artifactId> : java.lang.String
 *					version3 <version> : java.lang.String[0,1]
 *					type3 <type> : java.lang.String[0,1]
 *					import3 <import> : java.lang.String[0,1] 	[enumeration (classes), enumeration (services)]
 *			hiddenClasses3 <hidden-classes> : ClassFilterType[0,1]
 *				filter <filter> : java.lang.String[0,n]
 *				filter2 <filter> : java.lang.String[0,n]
 *				filter3 <filter> : java.lang.String[0,n]
 *			nonOverridableClasses3 <non-overridable-classes> : ClassFilterType[0,1]
 *				filter <filter> : java.lang.String[0,n]
 *				filter2 <filter> : java.lang.String[0,n]
 *				filter3 <filter> : java.lang.String[0,n]
 *			inverseClassloading3 <inverse-classloading> : EmptyType[0,1]
 *			suppressDefaultEnvironment3 <suppress-default-environment> : EmptyType[0,1]
 *		openejbJar <openejb-jar> : OpenejbJarType[0,1]
 *			any <any> : org.w3c.dom.Element 	[any namespace='##other' processContents='lax']
 *		abstractNamingEntry <abstract-naming-entry> : AbstractNamingEntryType[0,n]
 *		ejbRef <ejb-ref> : EjbRefType[0,n]
 *			refName <ref-name> : java.lang.String
 *			| pattern <pattern> : PatternType
 *			| 	groupId <groupId> : java.lang.String[0,1]
 *			| 	artifactId <artifactId> : java.lang.String[0,1]
 *			| 	version <version> : java.lang.String[0,1]
 *			| 	module <module> : java.lang.String[0,1]
 *			| 	name <name> : java.lang.String
 *			| nsCorbaloc <ns-corbaloc> : java.net.URI
 *			| name <name> : java.lang.String
 *			| | css <css> : PatternType
 *			| | 	groupId <groupId> : java.lang.String[0,1]
 *			| | 	artifactId <artifactId> : java.lang.String[0,1]
 *			| | 	version <version> : java.lang.String[0,1]
 *			| | 	module <module> : java.lang.String[0,1]
 *			| | 	name <name> : java.lang.String
 *			| | cssLink <css-link> : java.lang.String
 *			| ejbLink <ejb-link> : java.lang.String
 *		ejbLocalRef <ejb-local-ref> : EjbLocalRefType[0,n]
 *			refName <ref-name> : java.lang.String
 *			| pattern <pattern> : PatternType
 *			| 	groupId <groupId> : java.lang.String[0,1]
 *			| 	artifactId <artifactId> : java.lang.String[0,1]
 *			| 	version <version> : java.lang.String[0,1]
 *			| 	module <module> : java.lang.String[0,1]
 *			| 	name <name> : java.lang.String
 *			| ejbLink <ejb-link> : java.lang.String
 *		serviceRef <service-ref> : ServiceRefType[0,n]
 *			serviceRefName <service-ref-name> : java.lang.String
 *			| serviceCompletion <service-completion> : ServiceCompletionType
 *			| 	serviceName <service-name> : java.lang.String
 *			| 	portCompletion <port-completion> : PortCompletionType[1,n]
 *			| 		port <port> : PortType
 *			| 			portName <port-name> : java.lang.String
 *			| 			protocol <protocol> : java.lang.String
 *			| 			host <host> : java.lang.String
 *			| 			port <port> : int
 *			| 			uri <uri> : java.lang.String
 *			| 			credentialsName <credentials-name> : java.lang.String[0,1]
 *			| 		bindingName <binding-name> : java.lang.String
 *			| port <port> : PortType[1,n]
 *			| 	portName <port-name> : java.lang.String
 *			| 	protocol <protocol> : java.lang.String
 *			| 	host <host> : java.lang.String
 *			| 	port <port> : int
 *			| 	uri <uri> : java.lang.String
 *			| 	credentialsName <credentials-name> : java.lang.String[0,1]
 *		resourceRef <resource-ref> : ResourceRefType[0,n]
 *			refName <ref-name> : java.lang.String
 *			| pattern <pattern> : PatternType
 *			| 	groupId <groupId> : java.lang.String[0,1]
 *			| 	artifactId <artifactId> : java.lang.String[0,1]
 *			| 	version <version> : java.lang.String[0,1]
 *			| 	module <module> : java.lang.String[0,1]
 *			| 	name <name> : java.lang.String
 *			| resourceLink <resource-link> : java.lang.String
 *			| url <url> : java.lang.String
 *		resourceEnvRef <resource-env-ref> : ResourceEnvRefType[0,n]
 *			refName <ref-name> : java.lang.String
 *			| pattern <pattern> : PatternType
 *			| 	groupId <groupId> : java.lang.String[0,1]
 *			| 	artifactId <artifactId> : java.lang.String[0,1]
 *			| 	version <version> : java.lang.String[0,1]
 *			| 	module <module> : java.lang.String[0,1]
 *			| 	name <name> : java.lang.String
 *			| messageDestinationLink <message-destination-link> : java.lang.String
 *			| adminObjectModule <admin-object-module> : java.lang.String[0,1]
 *			| adminObjectLink <admin-object-link> : java.lang.String
 *		messageDestination <message-destination> : MessageDestinationType[0,n]
 *			messageDestinationName <message-destination-name> : java.lang.String
 *			| pattern <pattern> : PatternType
 *			| 	groupId <groupId> : java.lang.String[0,1]
 *			| 	artifactId <artifactId> : java.lang.String[0,1]
 *			| 	version <version> : java.lang.String[0,1]
 *			| 	module <module> : java.lang.String[0,1]
 *			| 	name <name> : java.lang.String
 *			| adminObjectModule <admin-object-module> : java.lang.String[0,1]
 *			| adminObjectLink <admin-object-link> : java.lang.String
 *		tssLink <tss-link> : TssLinkType[0,n]
 *			ejbName <ejb-name> : java.lang.String[0,1]
 *			tssName <tss-name> : java.lang.String[0,1]
 *			jndiName <jndi-name> : java.lang.String[0,n]
 *		webServiceBinding <web-service-binding> : WebServiceBindingType[0,n]
 *			ejbName <ejb-name> : java.lang.String
 *			webServiceAddress <web-service-address> : java.lang.String[0,1]
 *			webServiceVirtualHost <web-service-virtual-host> : java.lang.String[0,n]
 *			webServiceSecurity <web-service-security> : WebServiceSecurityType[0,1]
 *				securityRealmName <security-realm-name> : java.lang.String
 *				realmName <realm-name> : java.lang.String[0,1]
 *				transportGuarantee <transport-guarantee> : java.lang.String 	[enumeration (NONE), enumeration (INTEGRAL), enumeration (CONFIDENTIAL)]
 *				authMethod <auth-method> : java.lang.String 	[enumeration (BASIC), enumeration (DIGEST), enumeration (CLIENT-CERT), enumeration (NONE)]
 *		security <security> : AbstractSecurityType[0,1]
 *		(
 *		  | service <service> : AbstractServiceType
 *		  | persistence <persistence> : Persistence
 *		  | 	[attr: ee:version CDATA #FIXED 1.0 : java.lang.String] 	[pattern ([0-9]+(\.[0-9]+)*)]
 *		  | 	persistenceUnit <persistence-unit> : PersistenceUnit[0,n]
 *		  | 		[attr: ee:name CDATA #REQUIRED  : java.lang.String]
 *		  | 		[attr: ee:transaction-type CDATA #IMPLIED JTA : java.lang.String] 	[enumeration (JTA), enumeration (RESOURCE_LOCAL)]
 *		  | 		description <description> : java.lang.String[0,1]
 *		  | 		provider <provider> : java.lang.String[0,1]
 *		  | 		jtaDataSource <jta-data-source> : java.lang.String[0,1]
 *		  | 		nonJtaDataSource <non-jta-data-source> : java.lang.String[0,1]
 *		  | 		mappingFile <mapping-file> : java.lang.String[0,n]
 *		  | 		jarFile <jar-file> : java.lang.String[0,n]
 *		  | 		class2 <class> : java.lang.String[0,n]
 *		  | 		excludeUnlistedClasses <exclude-unlisted-classes> : boolean[0,1]
 *		  | 		properties <properties> : Properties[0,1]
 *		  | 			property2 <property> : Property[0,n]
 *		  | 				[attr: ee:name CDATA #REQUIRED  : java.lang.String]
 *		  | 				[attr: ee:value CDATA #REQUIRED  : java.lang.String]
 *		)[0,n]
 *
 * @Generated
 */

package org.netbeans.modules.j2ee.geronimo2.config.gen;

import org.w3c.dom.*;
import org.netbeans.modules.schema2beans.*;
import java.beans.*;
import java.util.*;
import java.io.*;

// BEGIN_NOI18N

public class EjbJar extends org.netbeans.modules.schema2beans.BaseBean
{

	static Vector comparators = new Vector();
	private static final org.netbeans.modules.schema2beans.Version runtimeVersion = new org.netbeans.modules.schema2beans.Version(5, 0, 0);

	static public final String ENVIRONMENT = "Environment";	// NOI18N
	static public final String OPENEJB_JAR = "OpenejbJar";	// NOI18N
	static public final String ABSTRACT_NAMING_ENTRY = "AbstractNamingEntry";	// NOI18N
	static public final String EJB_REF = "EjbRef";	// NOI18N
	static public final String EJB_LOCAL_REF = "EjbLocalRef";	// NOI18N
	static public final String SERVICE_REF = "ServiceRef";	// NOI18N
	static public final String RESOURCE_REF = "ResourceRef";	// NOI18N
	static public final String RESOURCE_ENV_REF = "ResourceEnvRef";	// NOI18N
	static public final String MESSAGE_DESTINATION = "MessageDestination";	// NOI18N
	static public final String TSS_LINK = "TssLink";	// NOI18N
	static public final String WEB_SERVICE_BINDING = "WebServiceBinding";	// NOI18N
	static public final String SECURITY = "Security";	// NOI18N
	static public final String SERVICE = "Service";	// NOI18N
	static public final String PERSISTENCE = "Persistence";	// NOI18N

	public EjbJar() {
		this(null, Common.USE_DEFAULT_VALUES);
	}

	public EjbJar(org.w3c.dom.Node doc, int options) {
		this(Common.NO_DEFAULT_VALUES);
		try {
			initFromNode(doc, options);
		}
		catch (Schema2BeansException e) {
			throw new RuntimeException(e);
		}
	}
	protected void initFromNode(org.w3c.dom.Node doc, int options) throws Schema2BeansException
	{
		if (doc == null)
		{
			doc = GraphManager.createRootElementNode("ejb-jar");	// NOI18N
			if (doc == null)
				throw new Schema2BeansException(Common.getMessage(
					"CantCreateDOMRoot_msg", "ejb-jar"));
		}
		Node n = GraphManager.getElementNode("ejb-jar", doc);	// NOI18N
		if (n == null)
			throw new Schema2BeansException(Common.getMessage(
				"DocRootNotInDOMGraph_msg", "ejb-jar", doc.getFirstChild().getNodeName()));

		this.graphManager.setXmlDocument(doc);

		// Entry point of the createBeans() recursive calls
		this.createBean(n, this.graphManager());
		this.initialize(options);
	}
	public EjbJar(int options)
	{
		super(comparators, runtimeVersion);
		initOptions(options);
	}
	protected void initOptions(int options)
	{
		// The graph manager is allocated in the bean root
		this.graphManager = new GraphManager(this);
		this.createRoot("ejb-jar", "EjbJar",	// NOI18N
			Common.TYPE_1 | Common.TYPE_BEAN, EjbJar.class);

		// Properties (see root bean comments for the bean graph)
		initPropertyTables(14);
		this.createProperty("environment", 	// NOI18N
			ENVIRONMENT, 
			Common.TYPE_0_1 | Common.TYPE_BEAN | Common.TYPE_KEY, 
			EnvironmentType.class);
		this.createProperty("openejb-jar", 	// NOI18N
			OPENEJB_JAR, 
			Common.TYPE_0_1 | Common.TYPE_BEAN | Common.TYPE_KEY, 
			OpenejbJarType.class);
		this.createProperty("abstract-naming-entry", 	// NOI18N
			ABSTRACT_NAMING_ENTRY, 
			Common.TYPE_0_N | Common.TYPE_BEAN | Common.TYPE_KEY, 
			AbstractNamingEntryType.class);
		this.createProperty("ejb-ref", 	// NOI18N
			EJB_REF, 
			Common.TYPE_0_N | Common.TYPE_BEAN | Common.TYPE_KEY, 
			EjbRefType.class);
		this.createProperty("ejb-local-ref", 	// NOI18N
			EJB_LOCAL_REF, 
			Common.TYPE_0_N | Common.TYPE_BEAN | Common.TYPE_KEY, 
			EjbLocalRefType.class);
		this.createProperty("service-ref", 	// NOI18N
			SERVICE_REF, 
			Common.TYPE_0_N | Common.TYPE_BEAN | Common.TYPE_KEY, 
			ServiceRefType.class);
		this.createProperty("resource-ref", 	// NOI18N
			RESOURCE_REF, 
			Common.TYPE_0_N | Common.TYPE_BEAN | Common.TYPE_KEY, 
			ResourceRefType.class);
		this.createProperty("resource-env-ref", 	// NOI18N
			RESOURCE_ENV_REF, 
			Common.TYPE_0_N | Common.TYPE_BEAN | Common.TYPE_KEY, 
			ResourceEnvRefType.class);
		this.createProperty("message-destination", 	// NOI18N
			MESSAGE_DESTINATION, 
			Common.TYPE_0_N | Common.TYPE_BEAN | Common.TYPE_KEY, 
			MessageDestinationType.class);
		this.createProperty("tss-link", 	// NOI18N
			TSS_LINK, 
			Common.TYPE_0_N | Common.TYPE_BEAN | Common.TYPE_KEY, 
			TssLinkType.class);
		this.createProperty("web-service-binding", 	// NOI18N
			WEB_SERVICE_BINDING, 
			Common.TYPE_0_N | Common.TYPE_BEAN | Common.TYPE_KEY, 
			WebServiceBindingType.class);
		this.createProperty("security", 	// NOI18N
			SECURITY, 
			Common.TYPE_0_1 | Common.TYPE_BEAN | Common.TYPE_KEY, 
			AbstractSecurityType.class);
		this.createProperty("service", 	// NOI18N
			SERVICE, Common.SEQUENCE_OR | 
			Common.TYPE_0_N | Common.TYPE_BEAN | Common.TYPE_KEY, 
			AbstractServiceType.class);
		this.createProperty("persistence", 	// NOI18N
			PERSISTENCE, Common.SEQUENCE_OR | 
			Common.TYPE_0_N | Common.TYPE_BEAN | Common.TYPE_KEY, 
			Persistence.class);
		this.createAttribute(PERSISTENCE, "ee:version", "EeVersion", 
						AttrProp.CDATA | AttrProp.FIXED,
						null, "1.0");
		this.initialize(options);
	}

	// Setting the default values of the properties
	void initialize(int options) {
		setDefaultNamespace("http://geronimo.apache.org/xml/ns/j2ee/ejb/openejb-2.0");

	}

	// This attribute is optional
	public void setEnvironment(EnvironmentType value) {
		this.setValue(ENVIRONMENT, value);
	}

	//
	public EnvironmentType getEnvironment() {
		return (EnvironmentType)this.getValue(ENVIRONMENT);
	}

	// This attribute is optional
	public void setOpenejbJar(OpenejbJarType value) {
		this.setValue(OPENEJB_JAR, value);
	}

	//
	public OpenejbJarType getOpenejbJar() {
		return (OpenejbJarType)this.getValue(OPENEJB_JAR);
	}

	// This attribute is an array, possibly empty
	public void setAbstractNamingEntry(int index, AbstractNamingEntryType value) {
		this.setValue(ABSTRACT_NAMING_ENTRY, index, value);
	}

	//
	public AbstractNamingEntryType getAbstractNamingEntry(int index) {
		return (AbstractNamingEntryType)this.getValue(ABSTRACT_NAMING_ENTRY, index);
	}

	// Return the number of properties
	public int sizeAbstractNamingEntry() {
		return this.size(ABSTRACT_NAMING_ENTRY);
	}

	// This attribute is an array, possibly empty
	public void setAbstractNamingEntry(AbstractNamingEntryType[] value) {
		this.setValue(ABSTRACT_NAMING_ENTRY, value);
	}

	//
	public AbstractNamingEntryType[] getAbstractNamingEntry() {
		return (AbstractNamingEntryType[])this.getValues(ABSTRACT_NAMING_ENTRY);
	}

	// Add a new element returning its index in the list
	public int addAbstractNamingEntry(org.netbeans.modules.j2ee.geronimo2.config.gen.AbstractNamingEntryType value) {
		int positionOfNewItem = this.addValue(ABSTRACT_NAMING_ENTRY, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeAbstractNamingEntry(org.netbeans.modules.j2ee.geronimo2.config.gen.AbstractNamingEntryType value) {
		return this.removeValue(ABSTRACT_NAMING_ENTRY, value);
	}

	// This attribute is an array, possibly empty
	public void setEjbRef(int index, EjbRefType value) {
		this.setValue(EJB_REF, index, value);
	}

	//
	public EjbRefType getEjbRef(int index) {
		return (EjbRefType)this.getValue(EJB_REF, index);
	}

	// Return the number of properties
	public int sizeEjbRef() {
		return this.size(EJB_REF);
	}

	// This attribute is an array, possibly empty
	public void setEjbRef(EjbRefType[] value) {
		this.setValue(EJB_REF, value);
	}

	//
	public EjbRefType[] getEjbRef() {
		return (EjbRefType[])this.getValues(EJB_REF);
	}

	// Add a new element returning its index in the list
	public int addEjbRef(org.netbeans.modules.j2ee.geronimo2.config.gen.EjbRefType value) {
		int positionOfNewItem = this.addValue(EJB_REF, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeEjbRef(org.netbeans.modules.j2ee.geronimo2.config.gen.EjbRefType value) {
		return this.removeValue(EJB_REF, value);
	}

	// This attribute is an array, possibly empty
	public void setEjbLocalRef(int index, EjbLocalRefType value) {
		this.setValue(EJB_LOCAL_REF, index, value);
	}

	//
	public EjbLocalRefType getEjbLocalRef(int index) {
		return (EjbLocalRefType)this.getValue(EJB_LOCAL_REF, index);
	}

	// Return the number of properties
	public int sizeEjbLocalRef() {
		return this.size(EJB_LOCAL_REF);
	}

	// This attribute is an array, possibly empty
	public void setEjbLocalRef(EjbLocalRefType[] value) {
		this.setValue(EJB_LOCAL_REF, value);
	}

	//
	public EjbLocalRefType[] getEjbLocalRef() {
		return (EjbLocalRefType[])this.getValues(EJB_LOCAL_REF);
	}

	// Add a new element returning its index in the list
	public int addEjbLocalRef(org.netbeans.modules.j2ee.geronimo2.config.gen.EjbLocalRefType value) {
		int positionOfNewItem = this.addValue(EJB_LOCAL_REF, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeEjbLocalRef(org.netbeans.modules.j2ee.geronimo2.config.gen.EjbLocalRefType value) {
		return this.removeValue(EJB_LOCAL_REF, value);
	}

	// This attribute is an array, possibly empty
	public void setServiceRef(int index, ServiceRefType value) {
		this.setValue(SERVICE_REF, index, value);
	}

	//
	public ServiceRefType getServiceRef(int index) {
		return (ServiceRefType)this.getValue(SERVICE_REF, index);
	}

	// Return the number of properties
	public int sizeServiceRef() {
		return this.size(SERVICE_REF);
	}

	// This attribute is an array, possibly empty
	public void setServiceRef(ServiceRefType[] value) {
		this.setValue(SERVICE_REF, value);
	}

	//
	public ServiceRefType[] getServiceRef() {
		return (ServiceRefType[])this.getValues(SERVICE_REF);
	}

	// Add a new element returning its index in the list
	public int addServiceRef(org.netbeans.modules.j2ee.geronimo2.config.gen.ServiceRefType value) {
		int positionOfNewItem = this.addValue(SERVICE_REF, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeServiceRef(org.netbeans.modules.j2ee.geronimo2.config.gen.ServiceRefType value) {
		return this.removeValue(SERVICE_REF, value);
	}

	// This attribute is an array, possibly empty
	public void setResourceRef(int index, ResourceRefType value) {
		this.setValue(RESOURCE_REF, index, value);
	}

	//
	public ResourceRefType getResourceRef(int index) {
		return (ResourceRefType)this.getValue(RESOURCE_REF, index);
	}

	// Return the number of properties
	public int sizeResourceRef() {
		return this.size(RESOURCE_REF);
	}

	// This attribute is an array, possibly empty
	public void setResourceRef(ResourceRefType[] value) {
		this.setValue(RESOURCE_REF, value);
	}

	//
	public ResourceRefType[] getResourceRef() {
		return (ResourceRefType[])this.getValues(RESOURCE_REF);
	}

	// Add a new element returning its index in the list
	public int addResourceRef(org.netbeans.modules.j2ee.geronimo2.config.gen.ResourceRefType value) {
		int positionOfNewItem = this.addValue(RESOURCE_REF, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeResourceRef(org.netbeans.modules.j2ee.geronimo2.config.gen.ResourceRefType value) {
		return this.removeValue(RESOURCE_REF, value);
	}

	// This attribute is an array, possibly empty
	public void setResourceEnvRef(int index, ResourceEnvRefType value) {
		this.setValue(RESOURCE_ENV_REF, index, value);
	}

	//
	public ResourceEnvRefType getResourceEnvRef(int index) {
		return (ResourceEnvRefType)this.getValue(RESOURCE_ENV_REF, index);
	}

	// Return the number of properties
	public int sizeResourceEnvRef() {
		return this.size(RESOURCE_ENV_REF);
	}

	// This attribute is an array, possibly empty
	public void setResourceEnvRef(ResourceEnvRefType[] value) {
		this.setValue(RESOURCE_ENV_REF, value);
	}

	//
	public ResourceEnvRefType[] getResourceEnvRef() {
		return (ResourceEnvRefType[])this.getValues(RESOURCE_ENV_REF);
	}

	// Add a new element returning its index in the list
	public int addResourceEnvRef(org.netbeans.modules.j2ee.geronimo2.config.gen.ResourceEnvRefType value) {
		int positionOfNewItem = this.addValue(RESOURCE_ENV_REF, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeResourceEnvRef(org.netbeans.modules.j2ee.geronimo2.config.gen.ResourceEnvRefType value) {
		return this.removeValue(RESOURCE_ENV_REF, value);
	}

	// This attribute is an array, possibly empty
	public void setMessageDestination(int index, MessageDestinationType value) {
		this.setValue(MESSAGE_DESTINATION, index, value);
	}

	//
	public MessageDestinationType getMessageDestination(int index) {
		return (MessageDestinationType)this.getValue(MESSAGE_DESTINATION, index);
	}

	// Return the number of properties
	public int sizeMessageDestination() {
		return this.size(MESSAGE_DESTINATION);
	}

	// This attribute is an array, possibly empty
	public void setMessageDestination(MessageDestinationType[] value) {
		this.setValue(MESSAGE_DESTINATION, value);
	}

	//
	public MessageDestinationType[] getMessageDestination() {
		return (MessageDestinationType[])this.getValues(MESSAGE_DESTINATION);
	}

	// Add a new element returning its index in the list
	public int addMessageDestination(org.netbeans.modules.j2ee.geronimo2.config.gen.MessageDestinationType value) {
		int positionOfNewItem = this.addValue(MESSAGE_DESTINATION, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeMessageDestination(org.netbeans.modules.j2ee.geronimo2.config.gen.MessageDestinationType value) {
		return this.removeValue(MESSAGE_DESTINATION, value);
	}

	// This attribute is an array, possibly empty
	public void setTssLink(int index, TssLinkType value) {
		this.setValue(TSS_LINK, index, value);
	}

	//
	public TssLinkType getTssLink(int index) {
		return (TssLinkType)this.getValue(TSS_LINK, index);
	}

	// Return the number of properties
	public int sizeTssLink() {
		return this.size(TSS_LINK);
	}

	// This attribute is an array, possibly empty
	public void setTssLink(TssLinkType[] value) {
		this.setValue(TSS_LINK, value);
	}

	//
	public TssLinkType[] getTssLink() {
		return (TssLinkType[])this.getValues(TSS_LINK);
	}

	// Add a new element returning its index in the list
	public int addTssLink(org.netbeans.modules.j2ee.geronimo2.config.gen.TssLinkType value) {
		int positionOfNewItem = this.addValue(TSS_LINK, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeTssLink(org.netbeans.modules.j2ee.geronimo2.config.gen.TssLinkType value) {
		return this.removeValue(TSS_LINK, value);
	}

	// This attribute is an array, possibly empty
	public void setWebServiceBinding(int index, WebServiceBindingType value) {
		this.setValue(WEB_SERVICE_BINDING, index, value);
	}

	//
	public WebServiceBindingType getWebServiceBinding(int index) {
		return (WebServiceBindingType)this.getValue(WEB_SERVICE_BINDING, index);
	}

	// Return the number of properties
	public int sizeWebServiceBinding() {
		return this.size(WEB_SERVICE_BINDING);
	}

	// This attribute is an array, possibly empty
	public void setWebServiceBinding(WebServiceBindingType[] value) {
		this.setValue(WEB_SERVICE_BINDING, value);
	}

	//
	public WebServiceBindingType[] getWebServiceBinding() {
		return (WebServiceBindingType[])this.getValues(WEB_SERVICE_BINDING);
	}

	// Add a new element returning its index in the list
	public int addWebServiceBinding(org.netbeans.modules.j2ee.geronimo2.config.gen.WebServiceBindingType value) {
		int positionOfNewItem = this.addValue(WEB_SERVICE_BINDING, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeWebServiceBinding(org.netbeans.modules.j2ee.geronimo2.config.gen.WebServiceBindingType value) {
		return this.removeValue(WEB_SERVICE_BINDING, value);
	}

	// This attribute is optional
	public void setSecurity(AbstractSecurityType value) {
		this.setValue(SECURITY, value);
	}

	//
	public AbstractSecurityType getSecurity() {
		return (AbstractSecurityType)this.getValue(SECURITY);
	}

	// This attribute is an array, possibly empty
	public void setService(int index, AbstractServiceType value) {
		this.setValue(SERVICE, index, value);
	}

	//
	public AbstractServiceType getService(int index) {
		return (AbstractServiceType)this.getValue(SERVICE, index);
	}

	// Return the number of properties
	public int sizeService() {
		return this.size(SERVICE);
	}

	// This attribute is an array, possibly empty
	public void setService(AbstractServiceType[] value) {
		this.setValue(SERVICE, value);
	}

	//
	public AbstractServiceType[] getService() {
		return (AbstractServiceType[])this.getValues(SERVICE);
	}

	// Add a new element returning its index in the list
	public int addService(org.netbeans.modules.j2ee.geronimo2.config.gen.AbstractServiceType value) {
		int positionOfNewItem = this.addValue(SERVICE, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeService(org.netbeans.modules.j2ee.geronimo2.config.gen.AbstractServiceType value) {
		return this.removeValue(SERVICE, value);
	}

	// This attribute is an array, possibly empty
	public void setPersistence(int index, Persistence value) {
		this.setValue(PERSISTENCE, index, value);
	}

	//
	public Persistence getPersistence(int index) {
		return (Persistence)this.getValue(PERSISTENCE, index);
	}

	// Return the number of properties
	public int sizePersistence() {
		return this.size(PERSISTENCE);
	}

	// This attribute is an array, possibly empty
	public void setPersistence(Persistence[] value) {
		this.setValue(PERSISTENCE, value);
	}

	//
	public Persistence[] getPersistence() {
		return (Persistence[])this.getValues(PERSISTENCE);
	}

	// Add a new element returning its index in the list
	public int addPersistence(org.netbeans.modules.j2ee.geronimo2.config.gen.Persistence value) {
		int positionOfNewItem = this.addValue(PERSISTENCE, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removePersistence(org.netbeans.modules.j2ee.geronimo2.config.gen.Persistence value) {
		return this.removeValue(PERSISTENCE, value);
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public EnvironmentType newEnvironmentType() {
		return new EnvironmentType();
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public OpenejbJarType newOpenejbJarType() {
		return new OpenejbJarType();
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public AbstractNamingEntryType newAbstractNamingEntryType() {
		return new AbstractNamingEntryType();
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public EjbRefType newEjbRefType() {
		return new EjbRefType();
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public EjbLocalRefType newEjbLocalRefType() {
		return new EjbLocalRefType();
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public ServiceRefType newServiceRefType() {
		return new ServiceRefType();
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public ResourceRefType newResourceRefType() {
		return new ResourceRefType();
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public ResourceEnvRefType newResourceEnvRefType() {
		return new ResourceEnvRefType();
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public MessageDestinationType newMessageDestinationType() {
		return new MessageDestinationType();
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public TssLinkType newTssLinkType() {
		return new TssLinkType();
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public WebServiceBindingType newWebServiceBindingType() {
		return new WebServiceBindingType();
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public AbstractSecurityType newAbstractSecurityType() {
		return new AbstractSecurityType();
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public AbstractServiceType newAbstractServiceType() {
		return new AbstractServiceType();
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public Persistence newPersistence() {
		return new Persistence();
	}

	//
	public static void addComparator(org.netbeans.modules.schema2beans.BeanComparator c) {
		comparators.add(c);
	}

	//
	public static void removeComparator(org.netbeans.modules.schema2beans.BeanComparator c) {
		comparators.remove(c);
	}
	//
	// This method returns the root of the bean graph
	// Each call creates a new bean graph from the specified DOM graph
	//
	public static EjbJar createGraph(org.w3c.dom.Node doc) {
		return new EjbJar(doc, Common.NO_DEFAULT_VALUES);
	}

	public static EjbJar createGraph(java.io.File f) throws java.io.IOException {
		java.io.InputStream in = new java.io.FileInputStream(f);
		try {
			return createGraph(in, false);
		} finally {
			in.close();
		}
	}

	public static EjbJar createGraph(java.io.InputStream in) {
		return createGraph(in, false);
	}

	public static EjbJar createGraph(java.io.InputStream in, boolean validate) {
		try {
			Document doc = GraphManager.createXmlDocument(in, validate);
			return createGraph(doc);
		}
		catch (Exception t) {
			throw new RuntimeException(Common.getMessage(
				"DOMGraphCreateFailed_msg",
				t));
		}
	}

	//
	// This method returns the root for a new empty bean graph
	//
	public static EjbJar createGraph() {
		return new EjbJar();
	}

	public void validate() throws org.netbeans.modules.schema2beans.ValidateException {
	}

	// Special serializer: output XML as serialization
	private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException{
		out.defaultWriteObject();
		final int MAX_SIZE = 0XFFFF;
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		write(baos);
		final byte [] array = baos.toByteArray();
		final int numStrings = array.length / MAX_SIZE;
		final int leftover = array.length % MAX_SIZE;
		out.writeInt(numStrings + (0 == leftover ? 0 : 1));
		out.writeInt(MAX_SIZE);
		int offset = 0;
		for (int i = 0; i < numStrings; i++){
			out.writeUTF(new String(array, offset, MAX_SIZE));
			offset += MAX_SIZE;
		}
		if (leftover > 0){
			final int count = array.length - offset;
			out.writeUTF(new String(array, offset, count));
		}
	}
	// Special deserializer: read XML as deserialization
	private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException{
		try{
			in.defaultReadObject();
			init(comparators, runtimeVersion);
			// init(comparators, new GenBeans.Version(1, 0, 8))
			final int numStrings = in.readInt();
			final int max_size = in.readInt();
			final StringBuffer sb = new StringBuffer(numStrings * max_size);
			for (int i = 0; i < numStrings; i++){
				sb.append(in.readUTF());
			}
			ByteArrayInputStream bais = new ByteArrayInputStream(sb.toString().getBytes());
			Document doc = GraphManager.createXmlDocument(bais, false);
			initOptions(Common.NO_DEFAULT_VALUES);
			initFromNode(doc, Common.NO_DEFAULT_VALUES);
		}
		catch (Schema2BeansException e){
			throw new RuntimeException(e);
		}
	}

	public void _setSchemaLocation(String location) {
		if (beanProp().getAttrProp("xsi:schemaLocation", true) == null) {
			createAttribute("xmlns:xsi", "xmlns:xsi", AttrProp.CDATA | AttrProp.IMPLIED, null, "http://www.w3.org/2001/XMLSchema-instance");
			setAttributeValue("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			createAttribute("xsi:schemaLocation", "xsi:schemaLocation", AttrProp.CDATA | AttrProp.IMPLIED, null, location);
		}
		setAttributeValue("xsi:schemaLocation", location);
	}

	public String _getSchemaLocation() {
		if (beanProp().getAttrProp("xsi:schemaLocation", true) == null) {
			createAttribute("xmlns:xsi", "xmlns:xsi", AttrProp.CDATA | AttrProp.IMPLIED, null, "http://www.w3.org/2001/XMLSchema-instance");
			setAttributeValue("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			createAttribute("xsi:schemaLocation", "xsi:schemaLocation", AttrProp.CDATA | AttrProp.IMPLIED, null, null);
		}
		return getAttributeValue("xsi:schemaLocation");
	}

	// Dump the content of this bean returning it as a String
	public void dump(StringBuffer str, String indent){
		String s;
		Object o;
		org.netbeans.modules.schema2beans.BaseBean n;
		str.append(indent);
		str.append("Environment");	// NOI18N
		n = (org.netbeans.modules.schema2beans.BaseBean) this.getEnvironment();
		if (n != null)
			n.dump(str, indent + "\t");	// NOI18N
		else
			str.append(indent+"\tnull");	// NOI18N
		this.dumpAttributes(ENVIRONMENT, 0, str, indent);

		str.append(indent);
		str.append("OpenejbJar");	// NOI18N
		n = (org.netbeans.modules.schema2beans.BaseBean) this.getOpenejbJar();
		if (n != null)
			n.dump(str, indent + "\t");	// NOI18N
		else
			str.append(indent+"\tnull");	// NOI18N
		this.dumpAttributes(OPENEJB_JAR, 0, str, indent);

		str.append(indent);
		str.append("AbstractNamingEntry["+this.sizeAbstractNamingEntry()+"]");	// NOI18N
		for(int i=0; i<this.sizeAbstractNamingEntry(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			n = (org.netbeans.modules.schema2beans.BaseBean) this.getAbstractNamingEntry(i);
			if (n != null)
				n.dump(str, indent + "\t");	// NOI18N
			else
				str.append(indent+"\tnull");	// NOI18N
			this.dumpAttributes(ABSTRACT_NAMING_ENTRY, i, str, indent);
		}

		str.append(indent);
		str.append("EjbRef["+this.sizeEjbRef()+"]");	// NOI18N
		for(int i=0; i<this.sizeEjbRef(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			n = (org.netbeans.modules.schema2beans.BaseBean) this.getEjbRef(i);
			if (n != null)
				n.dump(str, indent + "\t");	// NOI18N
			else
				str.append(indent+"\tnull");	// NOI18N
			this.dumpAttributes(EJB_REF, i, str, indent);
		}

		str.append(indent);
		str.append("EjbLocalRef["+this.sizeEjbLocalRef()+"]");	// NOI18N
		for(int i=0; i<this.sizeEjbLocalRef(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			n = (org.netbeans.modules.schema2beans.BaseBean) this.getEjbLocalRef(i);
			if (n != null)
				n.dump(str, indent + "\t");	// NOI18N
			else
				str.append(indent+"\tnull");	// NOI18N
			this.dumpAttributes(EJB_LOCAL_REF, i, str, indent);
		}

		str.append(indent);
		str.append("ServiceRef["+this.sizeServiceRef()+"]");	// NOI18N
		for(int i=0; i<this.sizeServiceRef(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			n = (org.netbeans.modules.schema2beans.BaseBean) this.getServiceRef(i);
			if (n != null)
				n.dump(str, indent + "\t");	// NOI18N
			else
				str.append(indent+"\tnull");	// NOI18N
			this.dumpAttributes(SERVICE_REF, i, str, indent);
		}

		str.append(indent);
		str.append("ResourceRef["+this.sizeResourceRef()+"]");	// NOI18N
		for(int i=0; i<this.sizeResourceRef(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			n = (org.netbeans.modules.schema2beans.BaseBean) this.getResourceRef(i);
			if (n != null)
				n.dump(str, indent + "\t");	// NOI18N
			else
				str.append(indent+"\tnull");	// NOI18N
			this.dumpAttributes(RESOURCE_REF, i, str, indent);
		}

		str.append(indent);
		str.append("ResourceEnvRef["+this.sizeResourceEnvRef()+"]");	// NOI18N
		for(int i=0; i<this.sizeResourceEnvRef(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			n = (org.netbeans.modules.schema2beans.BaseBean) this.getResourceEnvRef(i);
			if (n != null)
				n.dump(str, indent + "\t");	// NOI18N
			else
				str.append(indent+"\tnull");	// NOI18N
			this.dumpAttributes(RESOURCE_ENV_REF, i, str, indent);
		}

		str.append(indent);
		str.append("MessageDestination["+this.sizeMessageDestination()+"]");	// NOI18N
		for(int i=0; i<this.sizeMessageDestination(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			n = (org.netbeans.modules.schema2beans.BaseBean) this.getMessageDestination(i);
			if (n != null)
				n.dump(str, indent + "\t");	// NOI18N
			else
				str.append(indent+"\tnull");	// NOI18N
			this.dumpAttributes(MESSAGE_DESTINATION, i, str, indent);
		}

		str.append(indent);
		str.append("TssLink["+this.sizeTssLink()+"]");	// NOI18N
		for(int i=0; i<this.sizeTssLink(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			n = (org.netbeans.modules.schema2beans.BaseBean) this.getTssLink(i);
			if (n != null)
				n.dump(str, indent + "\t");	// NOI18N
			else
				str.append(indent+"\tnull");	// NOI18N
			this.dumpAttributes(TSS_LINK, i, str, indent);
		}

		str.append(indent);
		str.append("WebServiceBinding["+this.sizeWebServiceBinding()+"]");	// NOI18N
		for(int i=0; i<this.sizeWebServiceBinding(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			n = (org.netbeans.modules.schema2beans.BaseBean) this.getWebServiceBinding(i);
			if (n != null)
				n.dump(str, indent + "\t");	// NOI18N
			else
				str.append(indent+"\tnull");	// NOI18N
			this.dumpAttributes(WEB_SERVICE_BINDING, i, str, indent);
		}

		str.append(indent);
		str.append("Security");	// NOI18N
		n = (org.netbeans.modules.schema2beans.BaseBean) this.getSecurity();
		if (n != null)
			n.dump(str, indent + "\t");	// NOI18N
		else
			str.append(indent+"\tnull");	// NOI18N
		this.dumpAttributes(SECURITY, 0, str, indent);

		str.append(indent);
		str.append("Service["+this.sizeService()+"]");	// NOI18N
		for(int i=0; i<this.sizeService(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			n = (org.netbeans.modules.schema2beans.BaseBean) this.getService(i);
			if (n != null)
				n.dump(str, indent + "\t");	// NOI18N
			else
				str.append(indent+"\tnull");	// NOI18N
			this.dumpAttributes(SERVICE, i, str, indent);
		}

		str.append(indent);
		str.append("Persistence["+this.sizePersistence()+"]");	// NOI18N
		for(int i=0; i<this.sizePersistence(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			n = (org.netbeans.modules.schema2beans.BaseBean) this.getPersistence(i);
			if (n != null)
				n.dump(str, indent + "\t");	// NOI18N
			else
				str.append(indent+"\tnull");	// NOI18N
			this.dumpAttributes(PERSISTENCE, i, str, indent);
		}

	}
	public String dumpBeanNode(){
		StringBuffer str = new StringBuffer();
		str.append("EjbJar\n");	// NOI18N
		this.dump(str, "\n  ");	// NOI18N
		return str.toString();
	}}

// END_NOI18N


/*
		The following schema file has been used for generation:

<?xml version="1.0" encoding="UTF-8"?>
<!--

  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.

-->

<xs:schema
    xmlns:openejb="http://geronimo.apache.org/xml/ns/j2ee/ejb/openejb-2.0"
    targetNamespace="http://geronimo.apache.org/xml/ns/j2ee/ejb/openejb-2.0"
    xmlns:naming="http://geronimo.apache.org/xml/ns/naming-1.2"
    xmlns:app="http://geronimo.apache.org/xml/ns/j2ee/application-2.0"
    xmlns:sys="http://geronimo.apache.org/xml/ns/deployment-1.2"
    xmlns:ee="http://java.sun.com/xml/ns/persistence"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    elementFormDefault="qualified"
    attributeFormDefault="unqualified"
    version="1.0">

    <xs:import namespace="http://geronimo.apache.org/xml/ns/naming-1.2" schemaLocation="geronimo-naming-1.2.xsd"/>
    <xs:import namespace="http://geronimo.apache.org/xml/ns/j2ee/application-2.0" schemaLocation="geronimo-application-2.0.xsd"/>
    <xs:import namespace="http://geronimo.apache.org/xml/ns/deployment-1.2" schemaLocation="geronimo-module-1.2.xsd"/>
    <xs:import namespace="http://java.sun.com/xml/ns/persistence" schemaLocation="http://java.sun.com/xml/ns/persistence/persistence_1_0.xsd"/>

    <xs:element name="ejb-jar" type="openejb:geronimo-ejb-jarType"/>

    <xs:complexType name="emptyType"/>

    <xs:complexType name="geronimo-ejb-jarType">
        <xs:sequence>
            <xs:element ref="sys:environment" minOccurs="0"/>

            <xs:element name="openejb-jar" type="openejb:openejb-jarType" minOccurs="0"/>

            <!-- Naming -->
            <xs:group ref="naming:jndiEnvironmentRefsGroup" minOccurs="0" maxOccurs="unbounded"/>
            <xs:element ref="naming:message-destination" minOccurs="0" maxOccurs="unbounded"/>

            <xs:element name="tss-link" type="openejb:tss-linkType" minOccurs="0" maxOccurs="unbounded"/>

            <xs:element name="web-service-binding" type="openejb:web-service-bindingType" minOccurs="0" maxOccurs="unbounded"/>

            <!-- Security -->
            <xs:element ref="app:security" minOccurs="0"/>

            <!-- GBeans -->
            <xs:choice minOccurs="0" maxOccurs="unbounded">
                <xs:element ref="sys:service"/>
                <xs:element ref="ee:persistence"/>
            </xs:choice>
        </xs:sequence>
    </xs:complexType>

    <!-- TODO there is no need for the extra wrapper other then xmlbean is overly enforcing the unique particle attribution rule -->
    <xs:complexType name="openejb-jarType">
        <xs:sequence>
            <xs:any namespace="##other" processContents="lax"/>
        </xs:sequence>
    </xs:complexType>

    <xs:complexType name="tss-linkType">
        <xs:sequence>
            <xs:element name="ejb-name" type="xs:string" minOccurs="0"/>
            <xs:element name="tss-name" type="xs:string" minOccurs="0"/>
            <xs:element name="jndi-name" type="xs:string" minOccurs="0" maxOccurs="unbounded"/>
        </xs:sequence>
    </xs:complexType>

    <xs:complexType name="web-service-bindingType">
        <xs:sequence>
            <xs:element name="ejb-name" type="xs:string"/>
            <xs:element name="web-service-address" type="xs:string" minOccurs="0"/>
            <xs:element name="web-service-virtual-host" type="xs:string" minOccurs="0" maxOccurs="unbounded"/>
            <xs:element name="web-service-security" type="openejb:web-service-securityType" minOccurs="0"/>
        </xs:sequence>
    </xs:complexType>

    <xs:complexType name="web-service-securityType">
        <xs:sequence>
            <xs:element name="security-realm-name" type="xs:string"/>
            <xs:element name="realm-name" type="xs:string" minOccurs="0"/>
            <xs:element name="transport-guarantee" type="openejb:transport-guaranteeType"/>
            <xs:element name="auth-method" type="openejb:auth-methodType"/>
        </xs:sequence>
    </xs:complexType>

    <xs:simpleType name="transport-guaranteeType">
        <xs:restriction base="xs:string">
            <xs:enumeration value="NONE"/>
            <xs:enumeration value="INTEGRAL"/>
            <xs:enumeration value="CONFIDENTIAL"/>
        </xs:restriction>
    </xs:simpleType>

    <xs:simpleType name="auth-methodType">
        <xs:restriction base="xs:string">
            <xs:enumeration value="BASIC"/>
            <xs:enumeration value="DIGEST"/>
            <xs:enumeration value="CLIENT-CERT"/>
            <xs:enumeration value="NONE"/>
        </xs:restriction>
    </xs:simpleType>

</xs:schema>

*/
