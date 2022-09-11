# AxwayOneagentSDK
Dynatrace-oneagent
1.	copy jar files to /p01/app/axway762/apigateway/ext/lib/ on both Front and Back Gateways,
    •	aspectjweaver-1.8.13.jar
    •	AxwayAspectJ-4.9.jar
    •	oneagent-sdk-1.8.0.jar
2.	Add/update, /p01/app/axway762/apigateway/conf/ jvm.xml file, to add Dynatrace entries
Back Gateways:
      <ConfigurationFragment>
      <VMArg name="-DExpandMissingAttributeBehaviour=EMPTY_STRING"/>
      <VMArg name="-Dcom.axway.apimanager.api.data.cache=true"/>
      <VMArg name="-javaagent:/p01/app/axway762/apigateway/ext/lib/aspectjweaver-1.8.13.jar"/>
      </ConfigurationFragment>
Front Gateways:
    <ConfigurationFragment>
    <VMArg name="-DExpandMissingAttributeBehaviour=EMPTY_STRING"/>
    <VMArg name="-Dcom.axway.apimanager.api.data.cache=true"/>
    <VMArg name="-javaagent:/p01/app/axway762/apigateway/ext/lib/aspectjweaver-1.8.13.jar"/>
    <SystemProperty name="apimanager" value="false" />
    </ConfigurationFragment>

