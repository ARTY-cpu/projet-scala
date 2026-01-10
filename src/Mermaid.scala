import java.io.PrintWriter
import java.io.File

/**
 * Module de génération de fichiers Mermaid pour visualisation de graphes
 * 
 * Permet de créer des représentations visuelles des graphes de Markov
 * au format Mermaid (https://www.mermaidchart.com/)
 * 
 * Étape 3 du projet
 */
object Mermaid {
  
  /**
   * Convertit un numéro de sommet en identifiant alphabétique
   * 
   * Implémentation : VERSION 5 de getId_annexe.txt (tail-recursive optimisée)
   * Cette version est fonctionnelle, sans variables mutables, et optimisée par le compilateur.
   * 
   * Algorithme :
   * - 1 → "A"
   * - 2 → "B"
   * - ...
   * - 26 → "Z"
   * - 27 → "AA"
   * - 28 → "AB"
   * - ...
   * 
   * @param i numéro du sommet (base 1)
   * @return identifiant alphabétique
   * 
   * @example
   * {{{
   * getID(1)  // "A"
   * getID(26) // "Z"
   * getID(27) // "AA"
   * getID(52) // "AZ"
   * }}}
   */
  def getID(i: Int): String = {
    require(i > 0, s"Le numéro de sommet doit être positif: $i")
    
    @annotation.tailrec
    def loop(x: Int, acc: List[Char]): List[Char] = {
      if (x <= 0) acc
      else {
        val rem = (x - 1) % 26
        val letter = ('A' + rem).toChar
        loop((x - 1) / 26, letter :: acc)
      }
    }
    
    loop(i, Nil).mkString
  }
  
  /**
   * Crée le dossier de sortie s'il n'existe pas
   * 
   * @param dossier chemin du dossier à créer
   */
  private def creerDossierSortie(dossier: String): Unit = {
    val dir = new File(dossier)
    if (!dir.exists()) {
      dir.mkdirs()
    }
  }
  
  /**
   * Génère l'en-tête du fichier Mermaid avec la configuration
   * 
   * @return chaîne contenant l'en-tête formaté
   */
  private def genererEntete(): String = {
    """---
config:
   layout: elk
   theme: neo
   look: neo
---
flowchart LR
"""
  }
  
  /**
   * Génère la déclaration des sommets au format Mermaid
   * 
   * @param n nombre de sommets
   * @return chaîne avec toutes les déclarations de sommets
   * 
   * @example Résultat pour n=4 :
   * {{{
   * A((1))
   * B((2))
   * C((3))
   * D((4))
   * }}}
   */
  private def genererSommets(n: Int): String = {
    (1 to n).map { i =>
      s"${getID(i)}(($i))"
    }.mkString("\n")
  }
  
  /**
   * Génère les arêtes à partir d'un graphe en utilisant l'interface Graphe
   * 
   * @param graphe graphe de Markov
   * @return chaîne avec toutes les arêtes au format Mermaid
   * 
   * @example Résultat pour une transition :
   * {{{
   * A -->|0.95|A
   * }}}
   */
  private def genererAretes(graphe: Graphe): String = {
    val aretes = for {
      i <- 1 to graphe.nbSommets
      successeur <- graphe.successeurs(i)
      (j, proba) = successeur
    } yield {
      val idDepart = getID(i)
      val idArrivee = getID(j)
      // Format anglais avec point (pas virgule) pour Mermaid
      val probaFormatee = "%.2f".formatLocal(java.util.Locale.US, proba)
      s"$idDepart -->|$probaFormatee|$idArrivee"
    }
    aretes.mkString("\n")
  }
  
  /**
   * Génère le contenu complet du fichier Mermaid pour un graphe
   * 
   * @param graphe graphe de Markov
   * @return contenu du fichier au format Mermaid
   */
  private def genererContenu(graphe: Graphe): String = {
    val entete = genererEntete()
    val sommets = genererSommets(graphe.nbSommets)
    val aretes = genererAretes(graphe)
    
    s"$entete$sommets\n$aretes"
  }
  
  /**
   * Génère un fichier Mermaid à partir d'une MatriceAdjacence
   * 
   * Cette fonction traite la représentation par matrice d'adjacence.
   * 
   * @param matrice graphe sous forme de matrice d'adjacence
   * @param fichierSortie chemin du fichier de sortie (ex: "exemple1.mmd")
   * 
   * @example
   * {{{
   * val matrice = Chargeur.chargerMatrice("exemple1.txt").get
   * genererMermaidMatrice(matrice, "exemple1.mmd")
   * }}}
   */
  def genererMermaidMatrice(matrice: MatriceAdjacence, fichierSortie: String): Unit = {
    try {
      creerDossierSortie("mermaid")
      val cheminComplet = s"mermaid/$fichierSortie"
      val contenu = genererContenu(matrice)
      val writer = new PrintWriter(new File(cheminComplet))
      try {
        writer.write(contenu)
        println(s"Fichier Mermaid généré : $cheminComplet")
        println(s"  ${matrice.nbSommets} sommets")
        
        // Compter les arêtes
        val nbAretes = (1 to matrice.nbSommets).map { i =>
          matrice.successeurs(i).size
        }.sum
        println(s"  $nbAretes arêtes")
      } finally {
        writer.close()
      }
    } catch {
      case e: Exception =>
        println(s"Erreur lors de la génération du fichier : ${e.getMessage}")
    }
  }
  
  /**
   * Génère un fichier Mermaid à partir d'une ListeAdjacence
   * 
   * Cette fonction traite la représentation par liste d'adjacence.
   * 
   * @param liste graphe sous forme de liste d'adjacence
   * @param fichierSortie chemin du fichier de sortie (ex: "exemple1.mmd")
   * 
   * @example
   * {{{
   * val liste = Chargeur.chargerListe("exemple1.txt").get
   * genererMermaidListe(liste, "exemple1.mmd")
   * }}}
   */
  def genererMermaidListe(liste: ListeAdjacence, fichierSortie: String): Unit = {
    try {
      creerDossierSortie("mermaid")
      val cheminComplet = s"mermaid/$fichierSortie"
      val contenu = genererContenu(liste)
      val writer = new PrintWriter(new File(cheminComplet))
      try {
        writer.write(contenu)
        println(s"Fichier Mermaid généré : $cheminComplet")
        println(s"  ${liste.nbSommets} sommets")
        
        // Compter les arêtes
        val nbAretes = (1 to liste.nbSommets).map { i =>
          liste.successeurs(i).size
        }.sum
        println(s"  $nbAretes arêtes")
      } finally {
        writer.close()
      }
    } catch {
      case e: Exception =>
        println(s"Erreur lors de la génération du fichier : ${e.getMessage}")
    }
  }
  
  /**
   * Génère un fichier Mermaid depuis un fichier de graphe
   * 
   * Version pratique qui charge et génère en une seule étape.
   * 
   * @param fichierEntree fichier source (ex: "exemple1.txt")
   * @param fichierSortie fichier de sortie (ex: "exemple1.mmd")
   * @param useMatrice true pour MatriceAdjacence, false pour ListeAdjacence
   */
  def genererDepuisFichier(fichierEntree: String, fichierSortie: String, useMatrice: Boolean = true): Unit = {
    if (useMatrice) {
      Chargeur.chargerMatrice(fichierEntree) match {
        case Some(matrice) => genererMermaidMatrice(matrice, fichierSortie)
        case None => println(s"Erreur : impossible de charger $fichierEntree")
      }
    } else {
      Chargeur.chargerListe(fichierEntree) match {
        case Some(liste) => genererMermaidListe(liste, fichierSortie)
        case None => println(s"Erreur : impossible de charger $fichierEntree")
      }
    }
  }
  
  /**
   * Affiche un aperçu du contenu Mermaid généré (sans écrire de fichier)
   * 
   * Utile pour vérifier rapidement le résultat avant génération.
   * 
   * @param graphe graphe de Markov
   */
  def afficherApercu(graphe: Graphe): Unit = {
    println("=" * 70)
    println("APERÇU MERMAID")
    println("=" * 70)
    println(genererContenu(graphe))
    println("=" * 70)
  }
}
