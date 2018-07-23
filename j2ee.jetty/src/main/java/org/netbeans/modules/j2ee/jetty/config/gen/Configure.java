/**
 *	This generated bean class Configure matches the schema element 'Configure'.
 *
 *
 *	This class matches the root element of the DTD,
 *	and is the root of the following bean graph:
 *
 *	configure <Configure> : Configure
 *		[attr: class NMTOKEN #IMPLIED ]
 *		[attr: id NMTOKEN #IMPLIED ]
 *		(
 *		  | get <Get> : Get
 *		  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	(
 *		  | 	  | get <Get> : Get...
 *		  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | set <Set> : String
 *		  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | put <Put> : Put
 *		  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	(
 *		  | 	  | 	  | call <Call> : Call
 *		  | 	  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 	arg <Arg> : Arg[0,n]
 *		  | 	  | 	  | 		[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		(
 *		  | 	  | 	  | 		  | call <Call> : Call...
 *		  | 	  | 	  | 		  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | new <New> : New
 *		  | 	  | 	  | 		  | 	[attr: class NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	arg <Arg> : Arg[0,n]...
 *		  | 	  | 	  | 		  | 		[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	(
 *		  | 	  | 	  | 		  | 	  | get <Get> : Get...
 *		  | 	  | 	  | 		  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | set <Set> : String
 *		  | 	  | 	  | 		  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | put <Put> : Put...
 *		  | 	  | 	  | 		  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | call <Call> : Call...
 *		  | 	  | 	  | 		  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | new <New> : New...
 *		  | 	  | 	  | 		  | 	  | 	[attr: class NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | ref <Ref> : Ref
 *		  | 	  | 	  | 		  | 	  | 	[attr: id NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	(
 *		  | 	  | 	  | 		  | 	  | 	  | get <Get> : Get...
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | set <Set> : String
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | put <Put> : Put...
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | call <Call> : Call...
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | new <New> : New...
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: class NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | ref <Ref> : Ref...
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: id NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	  | array <Array> : Array
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 	item <Item> : Item[0,n]
 *		  | 	  | 	  | 		  | 	  | 	  | 		[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		(
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | call <Call> : Call...
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | new <New> : New...
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	[attr: class NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | get <Get> : Get...
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | systemProperty <SystemProperty> : boolean
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	[attr: default CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	EMPTY : String
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | array <Array> : Array...
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | ref <Ref> : Ref...
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	[attr: id NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | property2 <Property> : boolean
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	[attr: default CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	EMPTY : String
 *		  | 	  | 	  | 		  | 	  | 	  | 		)[0,n]
 *		  | 	  | 	  | 		  | 	  | 	  | property2 <Property> : boolean
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: default CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 	EMPTY : String
 *		  | 	  | 	  | 		  | 	  | 	)[0,n]
 *		  | 	  | 	  | 		  | 	  | array <Array> : Array
 *		  | 	  | 	  | 		  | 	  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	item <Item> : Item[0,n]
 *		  | 	  | 	  | 		  | 	  | 		[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		(
 *		  | 	  | 	  | 		  | 	  | 		  | call <Call> : Call...
 *		  | 	  | 	  | 		  | 	  | 		  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | new <New> : New...
 *		  | 	  | 	  | 		  | 	  | 		  | 	[attr: class NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | get <Get> : Get...
 *		  | 	  | 	  | 		  | 	  | 		  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | systemProperty <SystemProperty> : boolean
 *		  | 	  | 	  | 		  | 	  | 		  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	[attr: default CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	EMPTY : String
 *		  | 	  | 	  | 		  | 	  | 		  | array <Array> : Array...
 *		  | 	  | 	  | 		  | 	  | 		  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | ref <Ref> : Ref
 *		  | 	  | 	  | 		  | 	  | 		  | 	[attr: id NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	(
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | get <Get> : Get...
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | set <Set> : String
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | put <Put> : Put...
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | call <Call> : Call...
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | new <New> : New...
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: class NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | ref <Ref> : Ref...
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: id NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | array <Array> : Array...
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | property2 <Property> : boolean
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: default CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	EMPTY : String
 *		  | 	  | 	  | 		  | 	  | 		  | 	)[0,n]
 *		  | 	  | 	  | 		  | 	  | 		  | property2 <Property> : boolean
 *		  | 	  | 	  | 		  | 	  | 		  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	[attr: default CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	EMPTY : String
 *		  | 	  | 	  | 		  | 	  | 		)[0,n]
 *		  | 	  | 	  | 		  | 	  | property2 <Property> : boolean
 *		  | 	  | 	  | 		  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	[attr: default CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	EMPTY : String
 *		  | 	  | 	  | 		  | 	)[0,n]
 *		  | 	  | 	  | 		  | get <Get> : Get...
 *		  | 	  | 	  | 		  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | systemProperty <SystemProperty> : boolean
 *		  | 	  | 	  | 		  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	[attr: default CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	EMPTY : String
 *		  | 	  | 	  | 		  | array <Array> : Array
 *		  | 	  | 	  | 		  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	item <Item> : Item[0,n]
 *		  | 	  | 	  | 		  | 		[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 		[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		(
 *		  | 	  | 	  | 		  | 		  | call <Call> : Call...
 *		  | 	  | 	  | 		  | 		  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 		  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | new <New> : New
 *		  | 	  | 	  | 		  | 		  | 	[attr: class NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	arg <Arg> : Arg[0,n]...
 *		  | 	  | 	  | 		  | 		  | 		[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	(
 *		  | 	  | 	  | 		  | 		  | 	  | get <Get> : Get...
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | set <Set> : String
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | put <Put> : Put...
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | call <Call> : Call...
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | new <New> : New...
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: class NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | ref <Ref> : Ref
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: id NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	(
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | get <Get> : Get...
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | set <Set> : String
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | put <Put> : Put...
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | call <Call> : Call...
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | new <New> : New...
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: class NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | ref <Ref> : Ref...
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: id NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | array <Array> : Array...
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | property2 <Property> : boolean
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: default CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	EMPTY : String
 *		  | 	  | 	  | 		  | 		  | 	  | 	)[0,n]
 *		  | 	  | 	  | 		  | 		  | 	  | array <Array> : Array...
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | property2 <Property> : boolean
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: default CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	EMPTY : String
 *		  | 	  | 	  | 		  | 		  | 	)[0,n]
 *		  | 	  | 	  | 		  | 		  | get <Get> : Get...
 *		  | 	  | 	  | 		  | 		  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 		  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | systemProperty <SystemProperty> : boolean
 *		  | 	  | 	  | 		  | 		  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 		  | 	[attr: default CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	EMPTY : String
 *		  | 	  | 	  | 		  | 		  | array <Array> : Array...
 *		  | 	  | 	  | 		  | 		  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | ref <Ref> : Ref
 *		  | 	  | 	  | 		  | 		  | 	[attr: id NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 		  | 	(
 *		  | 	  | 	  | 		  | 		  | 	  | get <Get> : Get...
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | set <Set> : String
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | put <Put> : Put...
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | call <Call> : Call...
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | new <New> : New
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: class NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	arg <Arg> : Arg[0,n]...
 *		  | 	  | 	  | 		  | 		  | 	  | 		[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	(
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | get <Get> : Get...
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | set <Set> : String
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | put <Put> : Put...
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | call <Call> : Call...
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | new <New> : New...
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: class NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | ref <Ref> : Ref...
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: id NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | array <Array> : Array...
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | property2 <Property> : boolean
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: default CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	  | 	EMPTY : String
 *		  | 	  | 	  | 		  | 		  | 	  | 	)[0,n]
 *		  | 	  | 	  | 		  | 		  | 	  | ref <Ref> : Ref...
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: id NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 		  | 	  | array <Array> : Array...
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | property2 <Property> : boolean
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: default CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	  | 	EMPTY : String
 *		  | 	  | 	  | 		  | 		  | 	)[0,n]
 *		  | 	  | 	  | 		  | 		  | property2 <Property> : boolean
 *		  | 	  | 	  | 		  | 		  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 		  | 	[attr: default CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 		  | 	EMPTY : String
 *		  | 	  | 	  | 		  | 		)[0,n]
 *		  | 	  | 	  | 		  | ref <Ref> : Ref
 *		  | 	  | 	  | 		  | 	[attr: id NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	(
 *		  | 	  | 	  | 		  | 	  | get <Get> : Get...
 *		  | 	  | 	  | 		  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | set <Set> : String
 *		  | 	  | 	  | 		  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | put <Put> : Put...
 *		  | 	  | 	  | 		  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | call <Call> : Call...
 *		  | 	  | 	  | 		  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | new <New> : New
 *		  | 	  | 	  | 		  | 	  | 	[attr: class NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	arg <Arg> : Arg[0,n]...
 *		  | 	  | 	  | 		  | 	  | 		[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	(
 *		  | 	  | 	  | 		  | 	  | 	  | get <Get> : Get...
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | set <Set> : String
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | put <Put> : Put...
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | call <Call> : Call...
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | new <New> : New...
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: class NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | ref <Ref> : Ref...
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: id NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	  | array <Array> : Array
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 	item <Item> : Item[0,n]
 *		  | 	  | 	  | 		  | 	  | 	  | 		[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		(
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | call <Call> : Call...
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | new <New> : New...
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	[attr: class NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | get <Get> : Get...
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | systemProperty <SystemProperty> : boolean
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	[attr: default CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	EMPTY : String
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | array <Array> : Array...
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | ref <Ref> : Ref...
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	[attr: id NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | property2 <Property> : boolean
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	[attr: default CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 		  | 	EMPTY : String
 *		  | 	  | 	  | 		  | 	  | 	  | 		)[0,n]
 *		  | 	  | 	  | 		  | 	  | 	  | property2 <Property> : boolean
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: default CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	  | 	EMPTY : String
 *		  | 	  | 	  | 		  | 	  | 	)[0,n]
 *		  | 	  | 	  | 		  | 	  | ref <Ref> : Ref...
 *		  | 	  | 	  | 		  | 	  | 	[attr: id NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | array <Array> : Array
 *		  | 	  | 	  | 		  | 	  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	item <Item> : Item[0,n]
 *		  | 	  | 	  | 		  | 	  | 		[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		(
 *		  | 	  | 	  | 		  | 	  | 		  | call <Call> : Call...
 *		  | 	  | 	  | 		  | 	  | 		  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | new <New> : New
 *		  | 	  | 	  | 		  | 	  | 		  | 	[attr: class NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	arg <Arg> : Arg[0,n]...
 *		  | 	  | 	  | 		  | 	  | 		  | 		[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	(
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | get <Get> : Get...
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | set <Set> : String
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | put <Put> : Put...
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | call <Call> : Call...
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | new <New> : New...
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: class NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | ref <Ref> : Ref...
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: id NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | array <Array> : Array...
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | property2 <Property> : boolean
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: default CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	  | 	EMPTY : String
 *		  | 	  | 	  | 		  | 	  | 		  | 	)[0,n]
 *		  | 	  | 	  | 		  | 	  | 		  | get <Get> : Get...
 *		  | 	  | 	  | 		  | 	  | 		  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | systemProperty <SystemProperty> : boolean
 *		  | 	  | 	  | 		  | 	  | 		  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	[attr: default CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	EMPTY : String
 *		  | 	  | 	  | 		  | 	  | 		  | array <Array> : Array...
 *		  | 	  | 	  | 		  | 	  | 		  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | ref <Ref> : Ref...
 *		  | 	  | 	  | 		  | 	  | 		  | 	[attr: id NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 		  | property2 <Property> : boolean
 *		  | 	  | 	  | 		  | 	  | 		  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	[attr: default CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 		  | 	EMPTY : String
 *		  | 	  | 	  | 		  | 	  | 		)[0,n]
 *		  | 	  | 	  | 		  | 	  | property2 <Property> : boolean
 *		  | 	  | 	  | 		  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	  | 	[attr: default CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		  | 	)[0,n]
 *		  | 	  | 	  | 		  | property2 <Property> : boolean
 *		  | 	  | 	  | 		  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 		  | 	[attr: default CDATA #IMPLIED ]
 *		  | 	  | 	  | 		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 		)[0,n]
 *		  | 	  | 	  | 	(
 *		  | 	  | 	  | 	)[0,n]
 *		  | 	  | 	  | new <New> : New
 *		  | 	  | 	  | 	[attr: class NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | get <Get> : Get...
 *		  | 	  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | systemProperty <SystemProperty> : boolean
 *		  | 	  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 	[attr: default CDATA #IMPLIED ]
 *		  | 	  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | array <Array> : Array
 *		  | 	  | 	  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	  | ref <Ref> : Ref
 *		  | 	  | 	  | 	[attr: id NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | property2 <Property> : boolean
 *		  | 	  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	  | 	[attr: default CDATA #IMPLIED ]
 *		  | 	  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | 	)[0,n]
 *		  | 	  | call <Call> : Call
 *		  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | new <New> : New
 *		  | 	  | 	[attr: class NMTOKEN #REQUIRED ]
 *		  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | ref <Ref> : Ref
 *		  | 	  | 	[attr: id NMTOKEN #REQUIRED ]
 *		  | 	  | array <Array> : Array
 *		  | 	  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	  | property2 <Property> : boolean
 *		  | 	  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	  | 	[attr: default CDATA #IMPLIED ]
 *		  | 	  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | 	)[0,n]
 *		  | set <Set> : String
 *		  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | put <Put> : Put
 *		  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	[attr: type CDATA #IMPLIED ]
 *		  | call <Call> : Call
 *		  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	[attr: class NMTOKEN #IMPLIED ]
 *		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | new <New> : New
 *		  | 	[attr: class NMTOKEN #REQUIRED ]
 *		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | ref <Ref> : Ref
 *		  | 	[attr: id NMTOKEN #REQUIRED ]
 *		  | array <Array> : Array
 *		  | 	[attr: type CDATA #IMPLIED ]
 *		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		  | property2 <Property> : boolean
 *		  | 	[attr: name NMTOKEN #REQUIRED ]
 *		  | 	[attr: default CDATA #IMPLIED ]
 *		  | 	[attr: id NMTOKEN #IMPLIED ]
 *		)[0,n]
 *	... etc ...
 *
 * @Generated
 */

package org.netbeans.modules.j2ee.jetty.config.gen;

import org.w3c.dom.*;
import org.netbeans.modules.schema2beans.*;
import java.beans.*;
import java.util.*;
import java.io.*;
import org.netbeans.modules.j2ee.deployment.common.api.ConfigurationException;

// BEGIN_NOI18N

public class Configure extends org.netbeans.modules.schema2beans.BaseBean
{

	static Vector comparators = new Vector();
	private static final org.netbeans.modules.schema2beans.Version runtimeVersion = new org.netbeans.modules.schema2beans.Version(5, 0, 0);

	static public final String CLASS2 = "Class2";	// NOI18N
	static public final String ID = "Id";	// NOI18N
	static public final String GET = "Get";	// NOI18N
	static public final String SET = "Set";	// NOI18N
	static public final String SETNAME = "SetName";	// NOI18N
	static public final String SETTYPE = "SetType";	// NOI18N
	static public final String SETCLASS = "SetClass";	// NOI18N
	static public final String PUT = "Put";	// NOI18N
	static public final String CALL = "Call";	// NOI18N
	static public final String NEW = "New";	// NOI18N
	static public final String REF = "Ref";	// NOI18N
	static public final String ARRAY = "Array";	// NOI18N
	static public final String PROPERTY2 = "Property2";	// NOI18N
	static public final String PROPERTY2NAME = "Property2Name";	// NOI18N
	static public final String PROPERTY2DEFAULT = "Property2Default";	// NOI18N
	static public final String PROPERTY2ID = "Property2Id";	// NOI18N

	public Configure() {
		this(null, Common.USE_DEFAULT_VALUES);
	}

	public Configure(org.w3c.dom.Node doc, int options) {
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
			doc = GraphManager.createRootElementNode("Configure");	// NOI18N
			if (doc == null)
				throw new Schema2BeansException(Common.getMessage(
					"CantCreateDOMRoot_msg", "Configure"));
		}
		Node n = GraphManager.getElementNode("Configure", doc);	// NOI18N
		if (n == null)
			throw new Schema2BeansException(Common.getMessage(
				"DocRootNotInDOMGraph_msg", "Configure", doc.getFirstChild().getNodeName()));

		this.graphManager.setXmlDocument(doc);

		// Entry point of the createBeans() recursive calls
		this.createBean(n, this.graphManager());
		this.initialize(options);
	}
	public Configure(int options)
	{
		super(comparators, runtimeVersion);
		initOptions(options);
	}
	protected void initOptions(int options)
	{
		// The graph manager is allocated in the bean root
		this.graphManager = new GraphManager(this);
		this.createRoot("Configure", "Configure",	// NOI18N
			Common.TYPE_1 | Common.TYPE_BEAN, Configure.class);

		// Properties (see root bean comments for the bean graph)
		initPropertyTables(8);
		this.createProperty("Get", 	// NOI18N
			GET, Common.SEQUENCE_OR | 
			Common.TYPE_0_N | Common.TYPE_BEAN | Common.TYPE_KEY, 
			Get.class);
		this.createAttribute(GET, "name", "Name", 
						AttrProp.NMTOKEN | AttrProp.REQUIRED,
						null, null);
		this.createAttribute(GET, "class", "Class2", 
						AttrProp.NMTOKEN | AttrProp.IMPLIED,
						null, null);
		this.createAttribute(GET, "id", "Id", 
						AttrProp.NMTOKEN | AttrProp.IMPLIED,
						null, null);
		this.createProperty("Set", 	// NOI18N
			SET, Common.SEQUENCE_OR | 
			Common.TYPE_0_N | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createAttribute(SET, "name", "Name", 
						AttrProp.NMTOKEN | AttrProp.REQUIRED,
						null, null);
		this.createAttribute(SET, "type", "Type", 
						AttrProp.CDATA | AttrProp.IMPLIED,
						null, null);
		this.createAttribute(SET, "class", "Class", 
						AttrProp.NMTOKEN | AttrProp.IMPLIED,
						null, null);
		this.createProperty("Put", 	// NOI18N
			PUT, Common.SEQUENCE_OR | 
			Common.TYPE_0_N | Common.TYPE_BEAN | Common.TYPE_KEY, 
			Put.class);
		this.createAttribute(PUT, "name", "Name", 
						AttrProp.NMTOKEN | AttrProp.REQUIRED,
						null, null);
		this.createAttribute(PUT, "type", "Type", 
						AttrProp.CDATA | AttrProp.IMPLIED,
						null, null);
		this.createProperty("Call", 	// NOI18N
			CALL, Common.SEQUENCE_OR | 
			Common.TYPE_0_N | Common.TYPE_BEAN | Common.TYPE_KEY, 
			Call.class);
		this.createAttribute(CALL, "name", "Name", 
						AttrProp.NMTOKEN | AttrProp.REQUIRED,
						null, null);
		this.createAttribute(CALL, "class", "Class2", 
						AttrProp.NMTOKEN | AttrProp.IMPLIED,
						null, null);
		this.createAttribute(CALL, "id", "Id", 
						AttrProp.NMTOKEN | AttrProp.IMPLIED,
						null, null);
		this.createProperty("New", 	// NOI18N
			NEW, Common.SEQUENCE_OR | 
			Common.TYPE_0_N | Common.TYPE_BEAN | Common.TYPE_KEY, 
			New.class);
		this.createAttribute(NEW, "class", "Class2", 
						AttrProp.NMTOKEN | AttrProp.REQUIRED,
						null, null);
		this.createAttribute(NEW, "id", "Id", 
						AttrProp.NMTOKEN | AttrProp.IMPLIED,
						null, null);
		this.createProperty("Ref", 	// NOI18N
			REF, Common.SEQUENCE_OR | 
			Common.TYPE_0_N | Common.TYPE_BEAN | Common.TYPE_KEY, 
			Ref.class);
		this.createAttribute(REF, "id", "Id", 
						AttrProp.NMTOKEN | AttrProp.REQUIRED,
						null, null);
		this.createProperty("Array", 	// NOI18N
			ARRAY, Common.SEQUENCE_OR | 
			Common.TYPE_0_N | Common.TYPE_BEAN | Common.TYPE_KEY, 
			Array.class);
		this.createAttribute(ARRAY, "type", "Type", 
						AttrProp.CDATA | AttrProp.IMPLIED,
						null, null);
		this.createAttribute(ARRAY, "id", "Id", 
						AttrProp.NMTOKEN | AttrProp.IMPLIED,
						null, null);
		this.createProperty("Property", 	// NOI18N
			PROPERTY2, Common.SEQUENCE_OR | 
			Common.TYPE_0_N | Common.TYPE_BOOLEAN | Common.TYPE_KEY, 
			Boolean.class);
		this.createAttribute(PROPERTY2, "name", "Name", 
						AttrProp.NMTOKEN | AttrProp.REQUIRED,
						null, null);
		this.createAttribute(PROPERTY2, "default", "Default", 
						AttrProp.CDATA | AttrProp.IMPLIED,
						null, null);
		this.createAttribute(PROPERTY2, "id", "Id", 
						AttrProp.NMTOKEN | AttrProp.IMPLIED,
						null, null);
		this.createAttribute("class", "Class2", 
						AttrProp.NMTOKEN | AttrProp.IMPLIED,
						null, null);
		this.createAttribute("id", "Id", 
						AttrProp.NMTOKEN | AttrProp.IMPLIED,
						null, null);
		this.initialize(options);
	}

	// Setting the default values of the properties
	void initialize(int options) {

	}

	// This attribute is optional
	public void setClass2(java.lang.String value) {
		setAttributeValue(CLASS2, value);
	}

	//
	public java.lang.String getClass2() {
		return getAttributeValue(CLASS2);
	}

	// This attribute is optional
	public void setId(java.lang.String value) {
		setAttributeValue(ID, value);
	}

	//
	public java.lang.String getId() {
		return getAttributeValue(ID);
	}

	// This attribute is an array, possibly empty
	public void setGet(int index, Get value) {
		this.setValue(GET, index, value);
	}

	//
	public Get getGet(int index) {
		return (Get)this.getValue(GET, index);
	}

	// Return the number of properties
	public int sizeGet() {
		return this.size(GET);
	}

	// This attribute is an array, possibly empty
	public void setGet(Get[] value) {
		this.setValue(GET, value);
	}

	//
	public Get[] getGet() {
		return (Get[])this.getValues(GET);
	}

	// Add a new element returning its index in the list
	public int addGet(org.netbeans.modules.j2ee.jetty.config.gen.Get value) {
		int positionOfNewItem = this.addValue(GET, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeGet(org.netbeans.modules.j2ee.jetty.config.gen.Get value) {
		return this.removeValue(GET, value);
	}

	// This attribute is an array, possibly empty
	public void setSet(int index, String value) {
		this.setValue(SET, index, value);
	}

	//
	public String getSet(int index) {
		return (String)this.getValue(SET, index);
	}

	// Return the number of properties
	public int sizeSet() {
		return this.size(SET);
	}

	// This attribute is an array, possibly empty
	public void setSet(String[] value) {
		this.setValue(SET, value);
	}

	//
	public String[] getSet() {
		return (String[])this.getValues(SET);
	}

	// Add a new element returning its index in the list
	public int addSet(String value) {
		int positionOfNewItem = this.addValue(SET, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeSet(String value) {
		return this.removeValue(SET, value);
	}

	// This attribute is an array, possibly empty
	public void setSetName(int index, java.lang.String value) {
		// Make sure we've got a place to put this attribute.
		if (size(SET) == 0) {
			addValue(SET, "");
		}
		setAttributeValue(SET, index, "Name", value);
	}

	//
	public java.lang.String getSetName(int index) {
		// If our element does not exist, then the attribute does not exist.
		if (size(SET) == 0) {
			return null;
		} else {
			return getAttributeValue(SET, index, "Name");
		}
	}

	// Return the number of properties
	public int sizeSetName() {
		return this.size(SET);
	}

	// This attribute is an array, possibly empty
	public void setSetType(int index, java.lang.String value) {
		// Make sure we've got a place to put this attribute.
		if (size(SET) == 0) {
			addValue(SET, "");
		}
		setAttributeValue(SET, index, "Type", value);
	}

	//
	public java.lang.String getSetType(int index) {
		// If our element does not exist, then the attribute does not exist.
		if (size(SET) == 0) {
			return null;
		} else {
			return getAttributeValue(SET, index, "Type");
		}
	}

	// Return the number of properties
	public int sizeSetType() {
		return this.size(SET);
	}

	// This attribute is an array, possibly empty
	public void setSetClass(int index, java.lang.String value) {
		// Make sure we've got a place to put this attribute.
		if (size(SET) == 0) {
			addValue(SET, "");
		}
		setAttributeValue(SET, index, "Class", value);
	}

	//
	public java.lang.String getSetClass(int index) {
		// If our element does not exist, then the attribute does not exist.
		if (size(SET) == 0) {
			return null;
		} else {
			return getAttributeValue(SET, index, "Class");
		}
	}

	// Return the number of properties
	public int sizeSetClass() {
		return this.size(SET);
	}

	// This attribute is an array, possibly empty
	public void setPut(int index, Put value) {
		this.setValue(PUT, index, value);
	}

	//
	public Put getPut(int index) {
		return (Put)this.getValue(PUT, index);
	}

	// Return the number of properties
	public int sizePut() {
		return this.size(PUT);
	}

	// This attribute is an array, possibly empty
	public void setPut(Put[] value) {
		this.setValue(PUT, value);
	}

	//
	public Put[] getPut() {
		return (Put[])this.getValues(PUT);
	}

	// Add a new element returning its index in the list
	public int addPut(org.netbeans.modules.j2ee.jetty.config.gen.Put value) {
		int positionOfNewItem = this.addValue(PUT, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removePut(org.netbeans.modules.j2ee.jetty.config.gen.Put value) {
		return this.removeValue(PUT, value);
	}

	// This attribute is an array, possibly empty
	public void setCall(int index, Call value) {
		this.setValue(CALL, index, value);
	}

	//
	public Call getCall(int index) {
		return (Call)this.getValue(CALL, index);
	}

	// Return the number of properties
	public int sizeCall() {
		return this.size(CALL);
	}

	// This attribute is an array, possibly empty
	public void setCall(Call[] value) {
		this.setValue(CALL, value);
	}

	//
	public Call[] getCall() {
		return (Call[])this.getValues(CALL);
	}

	// Add a new element returning its index in the list
	public int addCall(org.netbeans.modules.j2ee.jetty.config.gen.Call value) {
		int positionOfNewItem = this.addValue(CALL, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeCall(org.netbeans.modules.j2ee.jetty.config.gen.Call value) {
		return this.removeValue(CALL, value);
	}

	// This attribute is an array, possibly empty
	public void setNew(int index, New value) {
		this.setValue(NEW, index, value);
	}

	//
	public New getNew(int index) {
		return (New)this.getValue(NEW, index);
	}

	// Return the number of properties
	public int sizeNew() {
		return this.size(NEW);
	}

	// This attribute is an array, possibly empty
	public void setNew(New[] value) {
		this.setValue(NEW, value);
	}

	//
	public New[] getNew() {
		return (New[])this.getValues(NEW);
	}

	// Add a new element returning its index in the list
	public int addNew(org.netbeans.modules.j2ee.jetty.config.gen.New value) {
		int positionOfNewItem = this.addValue(NEW, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeNew(org.netbeans.modules.j2ee.jetty.config.gen.New value) {
		return this.removeValue(NEW, value);
	}

	// This attribute is an array, possibly empty
	public void setRef(int index, Ref value) {
		this.setValue(REF, index, value);
	}

	//
	public Ref getRef(int index) {
		return (Ref)this.getValue(REF, index);
	}

	// Return the number of properties
	public int sizeRef() {
		return this.size(REF);
	}

	// This attribute is an array, possibly empty
	public void setRef(Ref[] value) {
		this.setValue(REF, value);
	}

	//
	public Ref[] getRef() {
		return (Ref[])this.getValues(REF);
	}

	// Add a new element returning its index in the list
	public int addRef(org.netbeans.modules.j2ee.jetty.config.gen.Ref value) {
		int positionOfNewItem = this.addValue(REF, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeRef(org.netbeans.modules.j2ee.jetty.config.gen.Ref value) {
		return this.removeValue(REF, value);
	}

	// This attribute is an array, possibly empty
	public void setArray(int index, Array value) {
		this.setValue(ARRAY, index, value);
	}

	//
	public Array getArray(int index) {
		return (Array)this.getValue(ARRAY, index);
	}

	// Return the number of properties
	public int sizeArray() {
		return this.size(ARRAY);
	}

	// This attribute is an array, possibly empty
	public void setArray(Array[] value) {
		this.setValue(ARRAY, value);
	}

	//
	public Array[] getArray() {
		return (Array[])this.getValues(ARRAY);
	}

	// Add a new element returning its index in the list
	public int addArray(org.netbeans.modules.j2ee.jetty.config.gen.Array value) {
		int positionOfNewItem = this.addValue(ARRAY, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeArray(org.netbeans.modules.j2ee.jetty.config.gen.Array value) {
		return this.removeValue(ARRAY, value);
	}

	// This attribute is an array, possibly empty
	public void setProperty2(int index, boolean value) {
		this.setValue(PROPERTY2, index, (value ? java.lang.Boolean.TRUE : java.lang.Boolean.FALSE));
	}

	//
	public boolean isProperty2(int index) {
		Boolean ret = (Boolean)this.getValue(PROPERTY2, index);
		if (ret == null)
			ret = (Boolean)Common.defaultScalarValue(Common.TYPE_BOOLEAN);
		return ((java.lang.Boolean)ret).booleanValue();
	}

	// Return the number of properties
	public int sizeProperty2() {
		return this.size(PROPERTY2);
	}

	// This attribute is an array, possibly empty
	public void setProperty2(boolean[] value) {
		Boolean[] values = null;
		if (value != null)
		{
			values = new Boolean[value.length];
			for (int i=0; i<value.length; i++)
				values[i] = (value[i] ? Boolean.TRUE : Boolean.FALSE);
		}
		this.setValue(PROPERTY2, values);
	}

	//
	public boolean[] getProperty2() {
		boolean[] ret = null;
		Boolean[] values = (Boolean[])this.getValues(PROPERTY2);
		if (values != null)
		{
			ret = new boolean[values.length];
			for (int i=0; i<values.length; i++)
				ret[i] = values[i].booleanValue();
		}
		return ret;
	}

	// Add a new element returning its index in the list
	public int addProperty2(boolean value) {
		int positionOfNewItem = this.addValue(PROPERTY2, (value ? java.lang.Boolean.TRUE : java.lang.Boolean.FALSE));
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeProperty2(boolean value) {
		return this.removeValue(PROPERTY2, (value ? java.lang.Boolean.TRUE : java.lang.Boolean.FALSE));
	}

	//
	// Remove an element using its index
	//
	public void removeProperty2(int index) {
		this.removeValue(PROPERTY2, index);
	}

	// This attribute is an array, possibly empty
	public void setProperty2Name(int index, java.lang.String value) {
		// Make sure we've got a place to put this attribute.
		if (size(PROPERTY2) == 0) {
			addValue(PROPERTY2, java.lang.Boolean.TRUE);
		}
		setValue(PROPERTY2, index, java.lang.Boolean.TRUE);
		setAttributeValue(PROPERTY2, index, "Name", value);
	}

	//
	public java.lang.String getProperty2Name(int index) {
		// If our element does not exist, then the attribute does not exist.
		if (size(PROPERTY2) == 0) {
			return null;
		} else {
			return getAttributeValue(PROPERTY2, index, "Name");
		}
	}

	// Return the number of properties
	public int sizeProperty2Name() {
		return this.size(PROPERTY2);
	}

	// This attribute is an array, possibly empty
	public void setProperty2Default(int index, java.lang.String value) {
		// Make sure we've got a place to put this attribute.
		if (size(PROPERTY2) == 0) {
			addValue(PROPERTY2, java.lang.Boolean.TRUE);
		}
		setValue(PROPERTY2, index, java.lang.Boolean.TRUE);
		setAttributeValue(PROPERTY2, index, "Default", value);
	}

	//
	public java.lang.String getProperty2Default(int index) {
		// If our element does not exist, then the attribute does not exist.
		if (size(PROPERTY2) == 0) {
			return null;
		} else {
			return getAttributeValue(PROPERTY2, index, "Default");
		}
	}

	// Return the number of properties
	public int sizeProperty2Default() {
		return this.size(PROPERTY2);
	}

	// This attribute is an array, possibly empty
	public void setProperty2Id(int index, java.lang.String value) {
		// Make sure we've got a place to put this attribute.
		if (size(PROPERTY2) == 0) {
			addValue(PROPERTY2, java.lang.Boolean.TRUE);
		}
		setValue(PROPERTY2, index, java.lang.Boolean.TRUE);
		setAttributeValue(PROPERTY2, index, "Id", value);
	}

	//
	public java.lang.String getProperty2Id(int index) {
		// If our element does not exist, then the attribute does not exist.
		if (size(PROPERTY2) == 0) {
			return null;
		} else {
			return getAttributeValue(PROPERTY2, index, "Id");
		}
	}

	// Return the number of properties
	public int sizeProperty2Id() {
		return this.size(PROPERTY2);
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public Get newGet() {
		return new Get();
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public Put newPut() {
		return new Put();
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public Call newCall() {
		return new Call();
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public New newNew() {
		return new New();
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public Ref newRef() {
		return new Ref();
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public Array newArray() {
		return new Array();
	}

        @SuppressWarnings("unchecked")
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
	public static Configure createGraph(org.w3c.dom.Node doc) {
		return new Configure(doc, Common.NO_DEFAULT_VALUES);
	}

	public static Configure createGraph(java.io.File f) throws java.io.IOException {
		java.io.InputStream in = new java.io.FileInputStream(f);
		try {
			return createGraph(in, false);
		} finally {
			in.close();
		}
	}

	public static Configure createGraph(java.io.InputStream in) {
		return createGraph(in, false);
	}

	public static Configure createGraph(java.io.InputStream in, boolean validate) {
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
	public static Configure createGraph() {
		return new Configure();
	}

		static public final String CONTEXTPATH = "contextPath";	// NOI18N
	
	public String getContextRoot() throws ConfigurationException {
		int idx = this.getContextPathIndex();
		if (idx<0 || (getSet().length==0) ) {
			throw new ConfigurationException("No ContextPath found in DD.");
		}
		return (String) this.getSet(idx);
	}
	
	public void setContextRoot(String contextRoot) {
		int idx = this.getContextPathIndex();
		if (idx<0) {
			idx=-idx;
			String []sets = this.getSet();
			String []temp = new String[sets.length+1];
			System.arraycopy(sets, 0, temp, 0, sets.length);
			temp[sets.length]=contextRoot;
			this.setSet(temp);
			this.setSetName(idx, CONTEXTPATH);
		} else {
			if (idx==0) {
				if (this.getSet().length==0) {
					this.setSet(new String[] {contextRoot});
					this.setSetName(idx, CONTEXTPATH);
				}
			}
			this.setSet(idx, contextRoot);
		}
	}
	
	private int getContextPathIndex() {
		String [] sets=getSet();
		if (sets.length==0)
			return 0;
		int i=0;
		for (; i < sets.length; i++) {
			if (getSetName(i).equals(CONTEXTPATH))
				return i;
		}
//		return negative number if no contextpath is set
//		can be even 0, this has to be handled in caller
		return -i;
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
		str.append("Get["+this.sizeGet()+"]");	// NOI18N
		for(int i=0; i<this.sizeGet(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			n = (org.netbeans.modules.schema2beans.BaseBean) this.getGet(i);
			if (n != null)
				n.dump(str, indent + "\t");	// NOI18N
			else
				str.append(indent+"\tnull");	// NOI18N
			this.dumpAttributes(GET, i, str, indent);
		}

		str.append(indent);
		str.append("Set["+this.sizeSet()+"]");	// NOI18N
		for(int i=0; i<this.sizeSet(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			str.append(indent+"\t");	// NOI18N
			str.append("<");	// NOI18N
			o = this.getSet(i);
			str.append((o==null?"null":o.toString().trim()));	// NOI18N
			str.append(">\n");	// NOI18N
			this.dumpAttributes(SET, i, str, indent);
		}

		str.append(indent);
		str.append("Put["+this.sizePut()+"]");	// NOI18N
		for(int i=0; i<this.sizePut(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			n = (org.netbeans.modules.schema2beans.BaseBean) this.getPut(i);
			if (n != null)
				n.dump(str, indent + "\t");	// NOI18N
			else
				str.append(indent+"\tnull");	// NOI18N
			this.dumpAttributes(PUT, i, str, indent);
		}

		str.append(indent);
		str.append("Call["+this.sizeCall()+"]");	// NOI18N
		for(int i=0; i<this.sizeCall(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			n = (org.netbeans.modules.schema2beans.BaseBean) this.getCall(i);
			if (n != null)
				n.dump(str, indent + "\t");	// NOI18N
			else
				str.append(indent+"\tnull");	// NOI18N
			this.dumpAttributes(CALL, i, str, indent);
		}

		str.append(indent);
		str.append("New["+this.sizeNew()+"]");	// NOI18N
		for(int i=0; i<this.sizeNew(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			n = (org.netbeans.modules.schema2beans.BaseBean) this.getNew(i);
			if (n != null)
				n.dump(str, indent + "\t");	// NOI18N
			else
				str.append(indent+"\tnull");	// NOI18N
			this.dumpAttributes(NEW, i, str, indent);
		}

		str.append(indent);
		str.append("Ref["+this.sizeRef()+"]");	// NOI18N
		for(int i=0; i<this.sizeRef(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			n = (org.netbeans.modules.schema2beans.BaseBean) this.getRef(i);
			if (n != null)
				n.dump(str, indent + "\t");	// NOI18N
			else
				str.append(indent+"\tnull");	// NOI18N
			this.dumpAttributes(REF, i, str, indent);
		}

		str.append(indent);
		str.append("Array["+this.sizeArray()+"]");	// NOI18N
		for(int i=0; i<this.sizeArray(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			n = (org.netbeans.modules.schema2beans.BaseBean) this.getArray(i);
			if (n != null)
				n.dump(str, indent + "\t");	// NOI18N
			else
				str.append(indent+"\tnull");	// NOI18N
			this.dumpAttributes(ARRAY, i, str, indent);
		}

		str.append(indent);
		str.append("Property2["+this.sizeProperty2()+"]");	// NOI18N
		for(int i=0; i<this.sizeProperty2(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			str.append(indent+"\t");	// NOI18N
			str.append((this.isProperty2(i)?"true":"false"));
			this.dumpAttributes(PROPERTY2, i, str, indent);
		}

	}
	public String dumpBeanNode(){
		StringBuffer str = new StringBuffer();
		str.append("Configure\n");	// NOI18N
		this.dump(str, "\n  ");	// NOI18N
		return str.toString();
	}}

// END_NOI18N


/*
		The following schema file has been used for generation:

<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
This is the document type descriptor for the
org.mortbay.util.XmlConfiguration class.  It allows a java object to be
configured by with a sequence of Set, Put and Call elements.  These tags are 
mapped to methods on the object to be configured as follows:

  <Set  name="Test">value</Set>              ==  obj.setTest("value");
  <Put  name="Test">value</Put>              ==  obj.put("Test","value");
  <Call name="test"><Arg>value</Arg></Call>  ==  obj.test("value");

Values themselves may be configured objects that are created with the
<New> tag or returned from a <Call> tag.

Values are matched to arguments on a best effort approach, but types
my be specified if a match is not achieved.

$Id: Configure.java,v 1.2 2008/04/29 06:42:17 cvsuser Exp $
-->

<!ENTITY % CONFIG "Get|Set|Put|Call|New|Ref|Array|Property">
<!ENTITY % VALUE "#PCDATA|Call|New|Get|SystemProperty|Array|Ref|Property">

<!ENTITY % TYPEATTR "type CDATA #IMPLIED " > <!-- String|Character|Short|Byte|Integer|Long|Boolean|Float|Double|char|short|byte|int|long|boolean|float|double|URL|InetAddress|InetAddrPort| #classname -->
<!ENTITY % IMPLIEDCLASSATTR "class NMTOKEN #IMPLIED" >
<!ENTITY % CLASSATTR "class NMTOKEN #REQUIRED" >
<!ENTITY % NAMEATTR "name NMTOKEN #REQUIRED" >
<!ENTITY % DEFAULTATTR "default CDATA #IMPLIED" >
<!ENTITY % IDATTR "id NMTOKEN #IMPLIED" >
<!ENTITY % REQUIREDIDATTR "id NMTOKEN #REQUIRED" >
<!--
Configure Element.
This is the root element that specifies the class of object that
can be configured:

    <Configure class="com.acme.MyClass"> ... </Configure>

A Configure element can contain Set, Put or Call elements.
-->
<!ELEMENT Configure (%CONFIG;)* >
<!ATTLIST Configure %IMPLIEDCLASSATTR; %IDATTR; >


<!--
Set Element.
This element maps to a call to a set method on the current object.
The name and optional type attributes are used to select the set 
method. If the name given is xxx, then a setXxx method is used, or
the xxx field is used of setXxx cannot be found. 
A Set element can contain value text and/or the value elements Call,
New and SystemProperty. If no value type is specified, then white
space is trimmed out of the value. If it contains multiple value
elements they are added as strings before being converted to any
specified type.

A Set with a class attribute is treated as a static set method invocation.
-->
<!ELEMENT Set (#PCDATA) >
<!ATTLIST Set %NAMEATTR; %TYPEATTR; %IMPLIEDCLASSATTR; >


<!--
Get Element.
This element maps to a call to a get method or field on the current object.
The name attribute is used to select the get method.
If the name given is xxx, then a getXxx method is used, or
the xxx field is used of setXxx cannot be found. 
A Get element can contain Set, Put and/or Call elements which act on the object
returned by the get call.

A Get with a class attribute is treated as a static get method or field.
-->
<!ELEMENT Get (%CONFIG;)*>
<!ATTLIST Get %NAMEATTR; %IMPLIEDCLASSATTR; %IDATTR; >

<!--
Put Element.
This element maps to a call to a put method on the current object,
which must implement the Map interface. The name attribute is used 
as the put key and the optional type attribute can force the type 
of the value.

A Put element can contain value text and/or the value elements Call,
New and SystemProperty. If no value type is specified, then white
space is trimmed out of the value. If it contains multiple value
elements they are added as strings before being converted to any
specified type.
-->
<!ELEMENT Put ( %VALUE; )* >
<!ATTLIST Put %NAMEATTR; %TYPEATTR; >


<!--
Call Element.
This element maps to an arbitrary call to a method on the current object,
The name attribute and Arg elements are used to select the method.

A Call element can contain a sequence of Arg elements followed by
a sequence of Set, Put and/or Call elements which act on any object
returned by the original call:

 <Call name="test"><Arg>value1</Arg><Set name="Test">Value2</Set></Call>

This is equivalent to:

 Object o2 = o1.test("value1");
 o2.setTest("value2");

A Call with a class attribute is treated as a static call.

-->
<!ELEMENT Call (Arg*,(%CONFIG;)*)>
<!ATTLIST Call %NAMEATTR; %IMPLIEDCLASSATTR; %IDATTR;>


<!--
Arg Element.
This element defines a positional argument for the Call element.
The optional type attribute can force the type of the value.

An Arg element can contain value text and/or the value elements Call,
New and SystemProperty. If no value type is specified, then white
space is trimmed out of the value. If it contains multiple value
elements they are added as strings before being converted to any
specified type.
-->
<!ELEMENT Arg ( %VALUE; )* >
<!ATTLIST Arg %TYPEATTR; >



<!--
New Element.
This element allows the creation of a new object as part of a 
value of a Set, Put or Arg element. The class attribute determines
the type of the new object and the contained Arg elements 
are used to select the constructor for the new object.

A New element can contain a sequence of Arg elements followed by
a sequence of Set, Put and/or Call elements which act on the new object:

 <New class="com.acme.MyClass">
   <Arg>value1</Arg><Set name="Test">Value2</Set>
 </New>

This is equivalent to:

 Object o = new com.acme.MyClass("value1");
 o.setTest("value2");

-->
<!ELEMENT New (Arg*,(%CONFIG;)*)>
<!ATTLIST New %CLASSATTR; %IDATTR;>

<!--
Ref Element.
This element allows a previously created object to be reference by id.

A Ref element can contain a sequence of Set, Put and/or Call elements 
which act on the referenced object:

 <Ref id="myobject">
   <Set name="Test">Value2</Set>
 </New>

-->
<!ELEMENT Ref ((%CONFIG;)*)>
<!ATTLIST Ref %REQUIREDIDATTR;>

<!--
Array Element.
This element allows the creation of a new array as part of a 
value of a Set, Put or Arg element. The type attribute determines
the type of the new array and the contained Item elements 
are used for each element of the array

 <Array type="java.lang.String">
   <Item>value0</Item>
   <Item><New class="java.lang.String"><Arg>value1</Arg></New></Item>
 </Array>

This is equivalent to:
 String[] a = new String[] { "value0", new String("value1") };

-->
<!ELEMENT Array (Item*)>
<!ATTLIST Array %TYPEATTR; %IDATTR; >

<!--
Map Element.
This element allows the creation of a new array as part of a 
value of a Set, Put or Arg element. The type attribute determines
the type of the new array and the contained Item elements 
are used for each element of the array

 <Map>
   <Entry>
     <Item>keyName</Item>
     <Item><New class="java.lang.String"><Arg>value1</Arg></New></Item>
   </Entry>
 </Map>

This is equivalent to:
 String[] a = new String[] { "value0", new String("value1") };

-->
<!ELEMENT Map (Entry*)>
<!ATTLIST Map %IDATTR; >
<!ELEMENT Entry (Item,Item)>


<!--
Item Element.
This element defines an entry for the Array or Map Entry elements.
The optional type attribute can force the type of the value.

An Item element can contain value text and/or the value elements Call,
New and SystemProperty. If no value type is specified, then white
space is trimmed out of the value. If it contains multiple value
elements they are added as strings before being converted to any
specified type.
-->
<!ELEMENT Item ( %VALUE; )* >
<!ATTLIST Item %TYPEATTR; %IDATTR; >


<!--
System Property Element.
This element allows JVM System properties to be retrieved as
part of the value of a Set, Put or Arg element.
The name attribute specifies the property name and the optional
default argument provides a default value.

 <SystemProperty name="Test" default="value"/>

This is equivalent to:

 System.getProperty("Test","value");

-->
<!ELEMENT SystemProperty EMPTY>
<!ATTLIST SystemProperty %NAMEATTR; %DEFAULTATTR; %IDATTR;>

<!--
Property Element.
This element allows arbitrary properties to be retrieved as
part of the value of a Set, Put or Arg element.
The name attribute specifies the property name and the optional
default argument provides a default value.

   <Property name="Test" default="value"/>
-->
<!ELEMENT Property EMPTY>
<!ATTLIST Property %NAMEATTR; %DEFAULTATTR; %IDATTR;>




*/
