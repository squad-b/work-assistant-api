# -*- coding: utf-8 -*-
import csv
import requests

# 책 검색 API에 결과가 없는 책
f1 = open("output/no_search_result_list.txt", "a")
f1.truncate(0)

# 검색 결과와 책 이름이 일치하는 책(대소문자 구별 없음)
f2 = open("output/match_title_list.txt", "a")
f2.truncate(0)

# 검색은 되지만 이름이 다른 책
f3 = open("output/mismath_title_list.txt", "a")
f3.truncate(0)

# 검색은 되지만 이름이 다른 책의 개수
result_file = open("output/generated_book_and_rental_info_inser_query.txt", "a")
result_file.truncate(0)

book_list_file = open('input/[미리디] 미리도서관_관리대장  - 도서목록.csv', 'r', encoding ='utf-8')
rows = csv.reader(book_list_file)

category = ''
stock_quantity = 0

for row in list(rows)[2:]:
    # if title == list(rows[index + 1])[1]:
    #     stock_quantity += 1
    #     continue
    title = row[1]

    # category 값 셋팅
    if (row[0] != '' and row[0] != None):
        category = row[0]

    api_url = f'{url}?query={title}&target=title'
    response = requests.get(api_url, headers = headers)
    response.encoding = 'utf-8'
    data = response.json()['documents']

    if len(data) == 0 or data is None:
        f1.write(title + '\n')

    is_mathced = False
    for datum in data:
        if (title.lower() == datum['title'].lower()):
            f2.write(title + '\n')

            # author 값 셋팅
            author_str = ''
            for author in datum['authors']:
                author_str += author + ','
            if author_str != '':
                author_str = author_str[0:len(author_str) - 1]

            insert_query = f'insert into book(isbn, title, description, author, stock_quantity, image_url, publishing_date, registration_date, publisher, category, registrant)' \
                           f' values(''{datum["isbn"]}'', ''{datum["title"]}'', ''{datum["contents"]}'', ''{author_str}'', stock_quantity, ''{datum["thumbnail"]}'', ''{datum["datetime"]}'', now(), ''{datum["publisher"]}'', ''{category}'', 1)'
            result_file.write(insert_query + '\n')
            is_mathced = True
            stock_quantity = 0
            break

    if is_mathced == False:
        f3.write(title + '\n')

book_list_file.close()
f1.close()
f2.close()
f3.close()