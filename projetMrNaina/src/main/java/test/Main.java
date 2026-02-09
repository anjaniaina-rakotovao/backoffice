// package test;

// import java.util.List;

// import methods.ScannerPackage;

// public class Main {
//     public static void main(String[] args) {
//         try {
//             // Scanner le package com.test
//             List<Class<?>> controllers = ScannerPackage.getAnnotatedClasses("test");

//             System.out.println("=== Classes avec @AnnotationController détectées ===");
//             for (Class<?> clazz : controllers) {
//                 System.out.println("→ " + clazz.getName());
//             }

//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }
// }