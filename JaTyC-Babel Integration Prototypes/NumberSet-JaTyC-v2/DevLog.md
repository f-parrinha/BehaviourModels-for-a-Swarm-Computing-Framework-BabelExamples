# DevLog


### Bug Found:
Assuming we have a Collection of object of type Integer (such as List), the following expression, due to nonNull(),
causes a crash:

<br>
int val = nonNull(uniqueNumbers.remove(idx));

<br>

This may happen due to unboxing/autoboxing, and the call intValue call. JaTyC assumes remove() returns Object | Null
 - Suggestion 1: since nonNull() remove the " | Null" part of the expected state, unboxing tries to call intValue on Object, which makes it crash.
 - Suggestion 2: "Integer removed = nonNull(uniqueNumbers.remove(idx));" also makes it crash, so it may be wrong
<br>

Crash Log:

<br>
java.lang.RuntimeException: debug
at jatyc.utils.JTCUtils$Companion.printStack(JTCUtils.kt:335)
at jatyc.core.typesystem.TypeInfo.checkJavaTypeInvariant(TypeInfo.kt:17)
at jatyc.core.linearmode.Store.set(Store.kt:283)
at jatyc.core.linearmode.Inference.analyzeCode(Inference.kt:516)
at jatyc.core.linearmode.LinearModeInference.analyzeCodeNode(LinearModeInference.kt:128)
at jatyc.core.linearmode.LinearModeInference.analyzeNode(LinearModeInference.kt:121)
at jatyc.core.cfg.CfgVisitor.analyze(CfgVisitor.kt:61)
at jatyc.core.cfg.CfgVisitor.analyze(CfgVisitor.kt:41)
at jatyc.core.linearmode.LinearModeClassAnalysis.analyzeMethod(LinearModeClassAnalysis.kt:249)
at jatyc.core.linearmode.LinearModeClassAnalysis.analyzeClassWithoutProtocol(LinearModeClassAnalysis.kt:90)
at jatyc.core.linearmode.LinearModeClassAnalysis.analyze(LinearModeClassAnalysis.kt:23)
at jatyc.core.adapters.CFVisitor.analyze(CFVisitor.kt:52)
at jatyc.core.adapters.CFVisitor.finishAnalysis(CFVisitor.kt:47)
at jatyc.JavaTypestateChecker.typeProcessingOver(JavaTypestateChecker.kt:85)
at org.checkerframework.javacutil.AbstractTypeProcessor$AttributionTaskListener.finished(AbstractTypeProcessor.java:191)
at jdk.compiler/com.sun.tools.javac.api.ClientCodeWrapper$WrappedTaskListener.finished(ClientCodeWrapper.java:828)
at jdk.compiler/com.sun.tools.javac.api.MultiTaskListener.finished(MultiTaskListener.java:132)
at jdk.compiler/com.sun.tools.javac.main.JavaCompiler.flow(JavaCompiler.java:1414)
at jdk.compiler/com.sun.tools.javac.main.JavaCompiler.flow(JavaCompiler.java:1371)
at jdk.compiler/com.sun.tools.javac.main.JavaCompiler.compile(JavaCompiler.java:973)
at jdk.compiler/com.sun.tools.javac.main.Main.compile(Main.java:311)
at jdk.compiler/com.sun.tools.javac.main.Main.compile(Main.java:170)
at jdk.compiler/com.sun.tools.javac.Main.compile(Main.java:57)
at jdk.compiler/com.sun.tools.javac.Main.main(Main.java:43)
Note: Some input files use unchecked or unsafe operations.
Note: Recompile with -Xlint:unchecked for details.
An exception has occurred in the compiler (11.0.20). Please file a bug against the Java compiler via the Java bug reporting page (https://bugreport.java.com) after checking the Bug Database (https://bugs.java.com) for duplicates. Include your program and the following diagnostic in your report. Thank you.
com.sun.tools.javac.util.ClientCodeException: java.lang.IllegalStateException: TypeInfo.javaType: expected java.lang.Object got java.lang.Integer
at jdk.compiler/com.sun.tools.javac.api.ClientCodeWrapper$WrappedTaskListener.finished(ClientCodeWrapper.java:832)
at jdk.compiler/com.sun.tools.javac.api.MultiTaskListener.finished(MultiTaskListener.java:132)
at jdk.compiler/com.sun.tools.javac.main.JavaCompiler.flow(JavaCompiler.java:1414)
at jdk.compiler/com.sun.tools.javac.main.JavaCompiler.flow(JavaCompiler.java:1371)
at jdk.compiler/com.sun.tools.javac.main.JavaCompiler.compile(JavaCompiler.java:973)
at jdk.compiler/com.sun.tools.javac.main.Main.compile(Main.java:311)
at jdk.compiler/com.sun.tools.javac.main.Main.compile(Main.java:170)
at jdk.compiler/com.sun.tools.javac.Main.compile(Main.java:57)
at jdk.compiler/com.sun.tools.javac.Main.main(Main.java:43)
Caused by: java.lang.IllegalStateException: TypeInfo.javaType: expected java.lang.Object got java.lang.Integer
at jatyc.core.typesystem.TypeInfo.checkJavaTypeInvariant(TypeInfo.kt:18)
at jatyc.core.linearmode.Store.set(Store.kt:283)
at jatyc.core.linearmode.Inference.analyzeCode(Inference.kt:516)
at jatyc.core.linearmode.LinearModeInference.analyzeCodeNode(LinearModeInference.kt:128)
at jatyc.core.linearmode.LinearModeInference.analyzeNode(LinearModeInference.kt:121)
at jatyc.core.cfg.CfgVisitor.analyze(CfgVisitor.kt:61)
at jatyc.core.cfg.CfgVisitor.analyze(CfgVisitor.kt:41)
at jatyc.core.linearmode.LinearModeClassAnalysis.analyzeMethod(LinearModeClassAnalysis.kt:249)
at jatyc.core.linearmode.LinearModeClassAnalysis.analyzeClassWithoutProtocol(LinearModeClassAnalysis.kt:90)
at jatyc.core.linearmode.LinearModeClassAnalysis.analyze(LinearModeClassAnalysis.kt:23)
at jatyc.core.adapters.CFVisitor.analyze(CFVisitor.kt:52)
at jatyc.core.adapters.CFVisitor.finishAnalysis(CFVisitor.kt:47)
at jatyc.JavaTypestateChecker.typeProcessingOver(JavaTypestateChecker.kt:85)
at org.checkerframework.javacutil.AbstractTypeProcessor$AttributionTaskListener.finished(AbstractTypeProcessor.java:191)
at jdk.compiler/com.sun.tools.javac.api.ClientCodeWrapper$WrappedTaskListener.finished(ClientCodeWrapper.java:828)
... 8 more