# shell.nix
{ pkgs ? import <nixpkgs> {} }:

pkgs.mkShell {
  buildInputs = with pkgs; [
    javaPackages.compiler.openjdk21
  ];
  JAVA_HOME = pkgs.javaPackages.compiler.openjdk21 + "/lib/openjdk";
}
