import matplotlib.pyplot as plt

les_diff = []

with open('resultat_est_spt_random.txt', 'r') as f:

    les_lignes = f.readlines()

    for ligne in les_lignes:
        t = ligne.strip().split(' ')
        makespan = int(t[0])
        
        ecart = t[1].replace(',', '.').strip()
        ecart = float(ecart)

        les_diff.append((ecart * 100) / makespan)
        

moyenne = sum(les_diff)/len(les_diff)

print(moyenne)
